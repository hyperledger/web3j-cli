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
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.web3j.console.account.AccountUtils;
import org.web3j.console.project.utils.InputVerifier;
import org.web3j.console.project.utils.ProjectUtils;

import static java.io.File.separator;
import static org.web3j.codegen.Console.exitError;

public class InteractiveOptions {

    private final InputVerifier inputVerifier;
    private final Scanner scanner;
    private final PrintStream outputStream;

    public InteractiveOptions() {
        this(System.out);
    }

    public InteractiveOptions(PrintStream printStream) {
        this(System.in, printStream);
    }

    public InteractiveOptions(InputStream inputStream, PrintStream outputStream) {
        this.scanner = new Scanner(inputStream);
        this.outputStream = outputStream;
        this.inputVerifier = new InputVerifier(outputStream);
    }

    public String getProjectName() {
        print("Please enter the project name [Web3App]:");
        String projectName = getUserInput();
        if (projectName.trim().isEmpty()) {
            return "Web3App";
        }
        while (!inputVerifier.classNameIsValid(projectName)) {
            projectName = getUserInput();
        }
        return projectName;
    }

    public String getPackageName() {
        print("Please enter the package name for your project [org.web3j]:");
        String packageName = getUserInput();
        if (packageName.trim().isEmpty()) {
            return "org.web3j";
        }
        while (!inputVerifier.packageNameIsValid(packageName)) {
            packageName = getUserInput();
        }
        return packageName;
    }

    public Optional<String> getProjectDestination(final String projectName) {
        print(
                "Please enter the destination of your project ["
                        + System.getProperty("user.dir")
                        + "]: ");
        final String projectDest = getUserInput();
        final String projectPath = projectDest + separator + projectName;
        if (new File(projectPath).exists()) {
            if (overrideExistingProject()) {
                Path path = new File(projectPath).toPath();
                ProjectUtils.deleteFolder(path);
                return Optional.of(projectDest);
            } else {
                exitError("Project creation was canceled.");
            }
        }
        return projectDest.isEmpty() ? Optional.empty() : Optional.of(projectDest);
    }

    public String createWallet(final String walletPath) {
        print("Please enter a wallet password.");
        String walletPassword = getUserInput();
        return createWallet(walletPath, walletPassword);
    }

    public String createWallet(final String walletPath, final String walletPassword) {
        return AccountUtils.accountDefaultWalletInit(walletPath, walletPassword);
    }

    public Map<String, String> getWalletLocation(final String defaultWalletPath) {
        Map<String, String> walletCredentials = new HashMap<>();
        if (userAnsweredYes("Would you like to use the default global wallet [Y/n] ?")) {
            if (!defaultWalletPath.isEmpty()) {
                walletCredentials.put("path", defaultWalletPath);
                walletCredentials.put("password", "");
                return walletCredentials;
            } else {
                if (userAnsweredYes(
                        "Looks like you don't have any global wallets. Would you like to generate one [Y/n] ?")) {
                    createWallet(defaultWalletPath, "");
                    walletCredentials.put("path", defaultWalletPath);
                    walletCredentials.put("password", "");
                    return walletCredentials;
                }
            }
        } else {
            print("Please enter your wallet path: ");
            String walletPath = getUserInput();
            if (walletPath.isEmpty() || !Files.exists(Paths.get(walletPath))) {
                print("Wallet path is invalid. Exiting ...");
                System.exit(1);
            }
            print(
                    "Please enter your wallet password [Leave empty if your wallet is not password protected]");
            String walletPassword = getUserInput();
            walletCredentials.put("path", walletPath);
            walletCredentials.put("password", walletPassword);
            return walletCredentials;
        }
        return walletCredentials;
    }

    public boolean userAnsweredYes(String message) {
        print(message);
        String answer = getUserInput();
        return answer.trim().isEmpty()
                || answer.trim().toLowerCase().equals("y")
                || answer.trim().toLowerCase().equals("yes");
    }

    public Optional<String> getGeneratedWrapperLocation() {
        print(
                "Please enter the path of the generated contract wrappers ["
                        + String.join(
                                separator,
                                System.getProperty("user.dir"),
                                "build",
                                "generated",
                                "source",
                                "web3j",
                                "main",
                                "java")
                        + "]");
        String pathToTheWrappers = getUserInput();
        return pathToTheWrappers.isEmpty()
                ? Optional.of(
                        String.join(
                                separator,
                                System.getProperty("user.dir"),
                                "build",
                                "generated",
                                "source",
                                "web3j",
                                "main",
                                "java"))
                : Optional.of(pathToTheWrappers);
    }

    public Optional<String> setGeneratedTestLocationJava() {
        print(
                "Where would you like to save your tests ["
                        + String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "java")
                        + "]");
        String outputPath = getUserInput();
        return outputPath.isEmpty()
                ? Optional.of(
                        String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "java"))
                : Optional.of(outputPath);
    }

    public Optional<String> setGeneratedTestLocationKotlin() {
        print(
                "Where would you like to save your tests ["
                        + String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "kotlin")
                        + "]");
        String outputPath = getUserInput();
        return outputPath.isEmpty()
                ? Optional.of(
                        String.join(
                                separator, System.getProperty("user.dir"), "src", "test", "kotlin"))
                : Optional.of(outputPath);
    }

    public boolean userWantsTests() {
        print("Would you like to generate unit test for your Solidity contracts [Y/n] ? ");
        String userAnswer = getUserInput();
        return userAnswer.trim().toLowerCase().equals("y") || userAnswer.trim().equals("");
    }

    public String getSolidityProjectPath() {
        print("Please enter the path to your Solidity file/folder [Required Field]: ");
        File file = new File(getUserInput());

        return file.getAbsolutePath();
    }

    public boolean overrideExistingProject() {
        print("Looks like the project exists. Would you like to overwrite it [y/N] ?");
        String userAnswer = getUserInput();
        return userAnswer.toLowerCase().equals("y");
    }

    public String getTokenName(String defaultValue) {
        print("Please enter the token name [" + defaultValue + "]: ");
        String tokenName = getUserInput();
        if (tokenName.isEmpty()) {
            return defaultValue;
        } else {
            return tokenName;
        }
    }

    public String getTokenSymbol(String defaultValue) {
        print("Please enter the token symbol [" + defaultValue + "]: ");
        String tokenSymbol = getUserInput();
        if (tokenSymbol.isEmpty()) {
            return defaultValue;
        } else {
            return tokenSymbol;
        }
    }

    public String getTokenInitialSupply(String defaultValue) {
        print("Please enter the token initial supply in Wei [" + defaultValue + "]: ");
        String supply = getUserInput();
        if (supply.isEmpty()) {
            return defaultValue;
        } else {
            return supply;
        }
    }

    public String[] getTokenDefaultOperators() {
        print(
                "Please enter the token default operators [your wallet address] (0x prefixed and ; separated): ");
        String operators = getUserInput();
        if (operators.isEmpty()) {
            return null;
        } else {
            return operators.split(";");
        }
    }

    protected String getUserInput() {
        return scanner.nextLine();
    }

    protected void print(final String text) {
        outputStream.println(text);
    }
}
