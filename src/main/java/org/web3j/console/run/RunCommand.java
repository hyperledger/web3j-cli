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
package org.web3j.console.run;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.diogonunes.jcdp.color.api.Ansi;
import com.google.common.annotations.VisibleForTesting;
import io.epirus.web3j.Epirus;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.account.AccountService;
import org.web3j.console.account.AccountUtils;
import org.web3j.console.account.subcommands.LoginCommand;
import org.web3j.console.project.utils.ProjectUtils;
import org.web3j.console.wallet.Faucet;
import org.web3j.console.wallet.subcommands.WalletFundCommand;
import org.web3j.console.wrapper.CredentialsOptions;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Network;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_OPENAPI_VAR_PREFIX;
import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_VAR_PREFIX;
import static org.web3j.console.config.ConfigManager.config;
import static org.web3j.console.project.utils.ProjectUtils.uploadSolidityMetadata;
import static org.web3j.console.utils.PrinterUtilities.*;
import static org.web3j.utils.Convert.Unit.ETHER;

@Command(
        name = "run",
        description = "Run your project using a live Ethereum network",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Epirus CLI is licensed under the Apache License 2.0")
public class RunCommand implements Runnable {
    @Mixin CredentialsOptions credentialsOptions;

    @Parameters(
            index = "0",
            paramLabel = "network",
            description = "Ethereum network [rinkeby/kovan]",
            arity = "1")
    String deployNetwork;

    private Path workingDirectory;
    private Network network;
    private AccountService accountService;
    private Credentials credentials;
    private Web3j web3j;

    @VisibleForTesting
    public RunCommand(
            Network network,
            AccountService accountService,
            Web3j web3j,
            Path workingDirectory,
            String walletPath) {
        this.workingDirectory = workingDirectory;
        this.network = network;
        this.credentialsOptions = new CredentialsOptions(Paths.get(walletPath), "", null, null);
        this.credentials = ProjectUtils.createCredentials(Paths.get(walletPath), "");
        this.accountService = accountService;
        this.web3j = web3j;
    }

    private RunCommand(
            Network network,
            AccountService accountService,
            Credentials credentials,
            Web3j web3j,
            CredentialsOptions credentialsOptions) {
        this.workingDirectory = Paths.get(System.getProperty("user.dir"));
        this.network = network;
        this.credentials = credentials;
        this.accountService = accountService;
        this.web3j = web3j;
        this.credentialsOptions = credentialsOptions;
    }

    public RunCommand() {}

    @Override
    public void run() {
        Web3j web3j = null;
        if (config.getLoginToken() == null || config.getLoginToken().length() == 0) {
            System.out.println(
                    "You aren't currently logged in to the Epirus Platform. Please create an account if you don't have one (https://portal.epirus.io/account/signup). If you do have an account, you can log in below:");
            new LoginCommand().run();
        }

        try {
            this.credentials = createCredentials();
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }

        try {
            web3j = Epirus.buildWeb3j(Network.valueOf(deployNetwork.toUpperCase()));
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
        try {
            new RunCommand(
                            Network.valueOf(deployNetwork.toUpperCase()),
                            new AccountService(),
                            credentials,
                            web3j,
                            credentialsOptions)
                    .deploy();
        } catch (Exception e) {
            printErrorAndExit(
                    "Epirus failed to deploy the project. For more information please see the log file.");
        }
    }

    private Credentials createCredentials() throws IOException, CipherException {
        if (credentialsOptions.getWalletPath() != null) {
            if (!credentialsOptions.getWalletPassword().isEmpty()) {
                return ProjectUtils.createCredentials(
                        credentialsOptions.getWalletPath(), credentialsOptions.getWalletPassword());
            }
            return ProjectUtils.createCredentials(credentialsOptions.getWalletPath(), "");
        } else if (!credentialsOptions.getRawKey().isEmpty()) {
            return Credentials.create(credentialsOptions.getRawKey());
        } else if (!credentialsOptions.getJson().isEmpty()) {
            if (!credentialsOptions.getWalletPassword().isEmpty()) {
                return WalletUtils.loadJsonCredentials(
                        credentialsOptions.getWalletPassword(), credentialsOptions.getJson());
            }
            return WalletUtils.loadJsonCredentials("", credentialsOptions.getJson());
        } else {
            return WalletUtils.loadCredentials(
                    config.getDefaultWalletPassword(), config.getDefaultWalletPath());
        }
    }

    public void deploy() throws Exception {
        coloredPrinter.println("Preparing to run your Web3App");
        System.out.print(System.lineSeparator());
        AccountUtils.accountInit(accountService);
        if (accountService.checkIfAccountIsConfirmed(20)) {
            printInformationPairWithStatus("Account status", 20, "ACTIVE ", Ansi.FColor.GREEN);
            System.out.print(System.lineSeparator());
        } else {
            printErrorAndExit(
                    "Please check your email and activate your account in order to take advantage our features. Once your account is activated you can re-run the command.");
        }
        fundWallet();
        uploadSolidityMetadata(network, workingDirectory);
        System.out.print(System.lineSeparator());
        coloredPrinter.println("Running your Web3App");
        System.out.print(System.lineSeparator());
        runGradle(workingDirectory);
    }

    private void fundWallet() {
        BigInteger accountBalance = accountService.getAccountBalance(credentials, web3j);
        printInformationPair(
                "Wallet balance",
                20,
                Convert.fromWei(String.valueOf(accountBalance), ETHER) + " ETH",
                Ansi.FColor.GREEN);
        try {
            if (accountBalance.equals(BigInteger.ZERO)) {
                String result =
                        WalletFundCommand.fundWallet(
                                credentials.getAddress(),
                                Faucet.valueOf(network.getNetworkName().toUpperCase()),
                                this.accountService.getLoginToken());
                printInformationPair("Funding wallet with", 20, "0.2 ETH", Ansi.FColor.GREEN);
                waitForBalanceUpdate(result);
            }

        } catch (Exception e) {
            printErrorAndExit("Could not fund wallet: " + e.getMessage());
        }
    }

    private void waitForBalanceUpdate(String txHash) {
        coloredPrinter.println(
                "Waiting for balance update",
                Ansi.Attribute.CLEAR,
                Ansi.FColor.YELLOW,
                Ansi.BColor.BLACK);
        try {
            System.out.printf("Waiting for transaction %s to be mined...\n", txHash);

            BigInteger accountBalance =
                    accountService.pollForAccountBalance(credentials, network, web3j, 5);

        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    private void runGradle(Path runLocation) throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            executeProcess(
                    new File(runLocation.toString()),
                    new String[] {"cmd", "/c", ".\\gradlew.bat run", "-q"});
        } else {
            executeProcess(
                    new File(File.separator, runLocation.toString()),
                    new String[] {"bash", "-c", "./gradlew run -q"});
        }
        Console.exitSuccess();
    }

    private void executeProcess(File workingDir, String[] command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        setEnvironment(processBuilder);

        int exitCode =
                processBuilder
                        .directory(workingDir)
                        .redirectErrorStream(true)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start()
                        .waitFor();
        if (exitCode != 0) {
            printErrorAndExit("Could not build project.");
        } else {
            printInformationPair(
                    "Wallet address",
                    20,
                    String.format(
                            "https://%s.epirus.io/accounts/%s",
                            network.getNetworkName(), credentials.getAddress()),
                    Ansi.FColor.BLUE);
        }
    }

    private void setEnvironment(final ProcessBuilder processBuilder) {
        if (credentialsOptions.getWalletPath() != null) {
            processBuilder
                    .environment()
                    .putIfAbsent(
                            WEB3J_VAR_PREFIX + "WALLET_PATH",
                            credentialsOptions.getWalletPath().toString());
            if (credentialsOptions.getWalletPassword() != null) {
                processBuilder
                        .environment()
                        .putIfAbsent(
                                WEB3J_VAR_PREFIX + "WALLET_PASSWORD",
                                credentialsOptions.getWalletPassword());
            }
        } else if (!credentialsOptions.getRawKey().isEmpty()) {
            processBuilder
                    .environment()
                    .putIfAbsent(WEB3J_VAR_PREFIX + "PRIVATE_KEY", credentialsOptions.getRawKey());
        } else if (!credentialsOptions.getJson().isEmpty()) {
            processBuilder
                    .environment()
                    .putIfAbsent(WEB3J_VAR_PREFIX + "WALLET_JSON", credentialsOptions.getJson());
        } else {
            processBuilder
                    .environment()
                    .putIfAbsent(WEB3J_VAR_PREFIX + "WALLET_PATH", config.getDefaultWalletPath());
            if (!config.getDefaultWalletPassword().isEmpty()) {
                processBuilder
                        .environment()
                        .putIfAbsent(
                                WEB3J_VAR_PREFIX + "WALLET_PASSWORD",
                                config.getDefaultWalletPassword());
            }
        }
        processBuilder
                .environment()
                .putIfAbsent(WEB3J_VAR_PREFIX + "NETWORK", network.getNetworkName());
        processBuilder
                .environment()
                .putIfAbsent(WEB3J_OPENAPI_VAR_PREFIX + "PORT", Integer.toString(9090));
        processBuilder.environment().putIfAbsent("EPIRUS_DEPLOY", String.valueOf(true));
    }
}
