/*
 * Copyright 2020 Web3 Labs Ltd.
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
package org.web3j.console.project.kotlin;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import picocli.CommandLine;

import org.web3j.console.project.BaseProject;
import org.web3j.console.project.InteractiveOptions;
import org.web3j.console.project.utils.InputVerifier;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.codegen.Console.exitSuccess;
import static org.web3j.utils.Collection.tail;

public class ProjectCreatorKotlin {
    public static final String COMMAND_NEW_KOTLIN = "kotlin";

    private final String root;
    private final String packageName;
    private final String projectName;

    ProjectCreatorKotlin(final String root, final String packageName, final String projectName) {
        this.projectName = projectName;
        this.packageName = packageName;
        this.root = root;
    }

    public static void main(String[] args) {
        final String projectName;
        final List<String> stringOptions = new ArrayList<>();
        if (args.length > 0 && args[0].equals(COMMAND_NEW_KOTLIN)) {
            args = tail(args);
            if (args.length == 0) {
                stringOptions.add("-n");
                projectName = InteractiveOptions.getProjectName();
                stringOptions.add(projectName);
                stringOptions.add("-p");
                stringOptions.add(InteractiveOptions.getPackageName());
                InteractiveOptions.getProjectDestination(projectName)
                        .ifPresent(
                                projectDest -> {
                                    stringOptions.add("-o");
                                    stringOptions.add(projectDest);
                                });
                args = stringOptions.toArray(new String[0]);
            }
        }
        CommandLine.run(new ProjectCreatorKotlinCLIRunner(), args);
    }

    void generate(
            boolean withTests,
            Optional<File> solidityFile,
            boolean withWalletProvider,
            boolean withFatJar,
            boolean withSampleCode) {
        try {
            KotlinBuilder kotlinBuilder =
                    (KotlinBuilder)
                            new KotlinBuilder()
                                    .withProjectName(this.projectName)
                                    .withRootDirectory(this.root)
                                    .withPackageName(this.packageName)
                                    .withTests(withTests)
                                    .withWalletProvider(withWalletProvider)
                                    .withCommand(ProjectCreatorKotlin.COMMAND_NEW_KOTLIN)
                                    .withSampleCode(withSampleCode)
                                    .withFatJar(withFatJar);
            solidityFile.map(File::getAbsolutePath).ifPresent(kotlinBuilder::withSolidityFile);
            KotlinProject kotlinProject = (KotlinProject) kotlinBuilder.build();
            kotlinProject.createProject();
            onSuccess(kotlinProject);
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("\nCould not generate project reason: \n" + sw.toString());
        }
    }

    private void onSuccess(BaseProject baseProject) {
        String address =
                baseProject.getProjectWallet() == null
                        ? ""
                        : ("\nYour wallet address is: "
                                + baseProject.getProjectWallet().getWalletAddress());

        exitSuccess(
                "\n"
                        + this.projectName
                        + " has been created in "
                        + this.root
                        + "\n"
                        + "To test your smart contracts (./src/test/java/io/web3j/generated/contracts/HelloWorldTest.java): ./gradlew test"
                        + "\n"
                        + "To run your Web3 app (./src/main/java/io/web3j/"
                        + InputVerifier.capitalizeFirstLetter(this.projectName)
                        + ".java): java -DNODE_URL=<URL_TO_NODE> -jar ./build/libs/"
                        + InputVerifier.capitalizeFirstLetter(this.projectName)
                        + "-0.1.0-all.jar\nTo fund your wallet on the Rinkeby test network go to: https://rinkeby.faucet.epirus.io/"
                        + address);
    }
}
