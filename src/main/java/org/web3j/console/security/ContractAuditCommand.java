/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.console.security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import ru.smartdec.smartcheck.RulesCached;
import ru.smartdec.smartcheck.RulesXml;
import ru.smartdec.smartcheck.app.*;
import ru.smartdec.smartcheck.app.cli.Tool;

import org.web3j.console.Web3jVersionProvider;

@Command(
        name = "audit",
        description = "Audit Solidity contract",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class ContractAuditCommand implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "solidity-file",
            description = "A file containing Solidity code")
    String filePath;

    @Override
    public void run() {
        try {
            Path source = Paths.get(filePath);
            Function<SourceLanguage, RulesXml.Source> defaultRules =
                    sourceLanguage ->
                            () -> {
                                String rulesFileName = sourceLanguage.rulesFileName();
                                URI uri = RulesXml.class.getResource(rulesFileName).toURI();
                                try {
                                    HashMap<String, String> env = new HashMap<>();
                                    env.put("create", "true");
                                    FileSystems.newFileSystem(uri, env);
                                } catch (FileSystemAlreadyExistsException ignored) {
                                }
                                return Paths.get(uri);
                            };

            final Integer[] totals = {0, 0};
            DefaultMedia media = new DefaultMedia(totals);
            new ReportDefault(
                            new DirectoryAnalysisCombined(
                                    makeDirectoryAnalysis(
                                            new SourceLanguages.Solidity(), source, defaultRules),
                                    makeDirectoryAnalysis(
                                            new SourceLanguages.Vyper(), source, defaultRules)),
                            media)
                    .print();

            if (media.getTotals()[1] > 0) {
                System.exit(-1);
            }
        } catch (Exception e) {
            System.err.println("The audit operation failed with the following exception:");
            e.printStackTrace();
        }
    }

    private static DirectoryAnalysis makeDirectoryAnalysis(
            final SourceLanguage sourceLanguage,
            final Path source,
            final Function<SourceLanguage, RulesXml.Source> rules)
            throws Exception {
        return new DirectoryAnalysisDefault(
                source,
                p -> p.toString().endsWith(sourceLanguage.fileExtension()),
                new TreeFactoryDefault(
                        DocumentBuilderFactory.newInstance().newDocumentBuilder(), sourceLanguage),
                new RulesCached(
                        new RulesXml(
                                rules.apply(sourceLanguage),
                                XPathFactory.newInstance().newXPath(),
                                Throwable::printStackTrace)));
    }
}

class DefaultMedia implements Media {

    Integer[] getTotals() {
        return totals;
    }

    private final Integer[] totals;

    DefaultMedia(final Integer[] totals) {
        this.totals = totals;
    }

    @Override
    public void accept(final DirectoryAnalysis.Info info) {
        LinkedList<List<String>> report_fields = new LinkedList<>();
        Map<String, Integer> result = new HashMap<>();
        info.treeReport()
                .streamUnchecked()
                .forEach(
                        tree ->
                                tree.contexts()
                                        .forEach(
                                                context -> {
                                                    LinkedList<String> fields = new LinkedList<>();
                                                    String rule_name;
                                                    try {
                                                        URL rule_name_resource =
                                                                Tool.class
                                                                        .getClassLoader()
                                                                        .getResource(
                                                                                String.format(
                                                                                        "rule_descriptions/%s/name_en.txt",
                                                                                        tree.rule()
                                                                                                .id()));
                                                        if (rule_name_resource != null) {
                                                            rule_name =
                                                                    new String(
                                                                            Files.readAllBytes(
                                                                                    Paths.get(
                                                                                            rule_name_resource
                                                                                                    .toURI())));
                                                        } else {
                                                            rule_name = "";
                                                        }
                                                    } catch (IOException | URISyntaxException e) {
                                                        rule_name = "";
                                                    }
                                                    fields.addLast("");
                                                    fields.addLast(
                                                            String.format(
                                                                    "%d:%d",
                                                                    context.getStart().getLine(),
                                                                    context.getStart()
                                                                            .getCharPositionInLine()));
                                                    fields.addLast(
                                                            String.format(
                                                                    "severity:%d",
                                                                    tree.pattern().severity()));
                                                    if (tree.pattern().severity() > 1) {
                                                        totals[1]++;
                                                    }
                                                    fields.addLast(rule_name);
                                                    fields.addLast(
                                                            String.format(
                                                                    "%s_%s",
                                                                    tree.rule().id(),
                                                                    tree.pattern().id()));
                                                    result.compute(
                                                            tree.rule().id(),
                                                            (k, v) ->
                                                                    Optional.ofNullable(v)
                                                                            .map(i -> i + 1)
                                                                            .orElse(1));
                                                    report_fields.addLast(fields);
                                                }));
        if (!report_fields.isEmpty()) {
            System.out.println(info.file());
            System.out.print(formatAsTable(report_fields));
            totals[0] += report_fields.size();
        }
    }

    private static String formatAsTable(List<List<String>> rows) {
        if (rows.isEmpty()) return "";
        int[] maxLengths = new int[rows.get(0).size()];
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        StringBuilder formatBuilder = new StringBuilder();
        for (int maxLength : maxLengths) {
            formatBuilder.append("%-").append(maxLength + 3).append("s");
        }
        String format = formatBuilder.toString();

        StringBuilder result = new StringBuilder();
        for (List<String> row : rows) {
            String[] res = row.toArray(new String[0]);
            result.append(String.format(format, (Object[]) res)).append("\n");
        }
        return result.toString();
    }
}
