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
package org.web3j.console.project;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import org.web3j.console.project.java.JavaBuilder;
import org.web3j.console.project.java.JavaProjectCreatorCLIRunner;
import org.web3j.console.project.kotlin.KotlinBuilder;
import org.web3j.console.project.kotlin.KotlinProjectCreatorCLIRunner;
import org.web3j.console.project.utils.InputVerifier;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.codegen.Console.exitSuccess;
import static org.web3j.utils.Collection.tail;

public class ProjectCreator {

    public static final String COMMAND_NEW = "new";
    public static final String COMMAND_JAVA = "--java";
    public static final String COMMAND_KOTLIN = "kotlin";
    private final String root;
    private final String packageName;
    private final String projectName;

    public ProjectCreator(final String root, final String packageName, final String projectName) {
        this.projectName = projectName;
        this.packageName = packageName;
        this.root = root;
    }

    public static void main(String[] args) throws IOException {
        final List<String> stringOptions = new ArrayList<>();
        if (args.length > 0 && args[0].toLowerCase().equals(COMMAND_JAVA)) {
            args = tail(args);
            args = getValues(args, stringOptions);
            CommandLine.run(new JavaProjectCreatorCLIRunner(), args);
        } else {
            args = getValues(args, stringOptions);
            CommandLine.run(new KotlinProjectCreatorCLIRunner(), args);
        }
    }

    @NotNull
    private static String[] getValues(String[] args, List<String> stringOptions)
            throws IOException {
        String projectName;
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
        return args;
    }

    public void generateJava(
            boolean withTests,
            Optional<File> solidityFile,
            boolean withWalletProvider,
            boolean withFatJar,
            boolean withSampleCode,
            String command) {
        try {
            JavaBuilder javaBuilder =
                    new JavaBuilder()
                            .withProjectName(this.projectName)
                            .withRootDirectory(this.root)
                            .withPackageName(this.packageName)
                            .withTests(withTests)
                            .withWalletProvider(withWalletProvider)
                            .withCommand(command)
                            .withSampleCode(withSampleCode)
                            .withFatJar(withFatJar);
            solidityFile.map(File::getAbsolutePath).ifPresent(javaBuilder::withSolidityFile);

            Project javaProject = javaBuilder.build();
            javaProject.createProject();
            onSuccess(javaProject);
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("\nCould not generate project reason: \n" + sw.toString());
        }
    }

    public void generateKotlin(
            boolean withTests,
            Optional<File> solidityFile,
            boolean withWalletProvider,
            boolean withFatJar,
            boolean withSampleCode,
            String command) {
        try {
            KotlinBuilder kotlinBuilder =
                    new KotlinBuilder()
                            .withProjectName(this.projectName)
                            .withRootDirectory(this.root)
                            .withPackageName(this.packageName)
                            .withTests(withTests)
                            .withWalletProvider(withWalletProvider)
                            .withCommand(command)
                            .withSampleCode(withSampleCode)
                            .withFatJar(withFatJar);
            solidityFile.map(File::getAbsolutePath).ifPresent(kotlinBuilder::withSolidityFile);
            Project kotlinProject = kotlinBuilder.build();
            kotlinProject.createProject();
            onSuccess(kotlinProject);
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("\nCould not generate project reason: \n" + sw.toString());
        }
    }

    private void onSuccess(Project javaProject) {
        String address =
                javaProject.getProjectWallet() == null
                        ? ""
                        : ("\nYour wallet address is: "
                                + javaProject.getProjectWallet().getWalletAddress());

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
