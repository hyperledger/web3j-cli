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
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.annotations.VisibleForTesting;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;

import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_OPENAPI_VAR_PREFIX;
import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_VAR_PREFIX;
import static org.web3j.console.utils.PrinterUtilities.printErrorAndExit;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;

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
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class RunCommand implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "network",
            description = "Ethereum network endpoint",
            arity = "1")
    String nodeURL;

    @Parameters(
            index = "1",
            paramLabel = "wallet-path",
            description = "Absolute path to your wallet file",
            arity = "1")
    String walletPath;

    @Parameters(
            index = "2",
            paramLabel = "wallet-password",
            description = "Wallet password",
            arity = "1",
            defaultValue = "")
    String walletPassword;

    static {
        AnsiConsole.systemInstall();
    }

    private Path workingDirectory = Paths.get(System.getProperty("user.dir"));

    @VisibleForTesting
    public RunCommand(Path workingDirectory, String walletPath) {
        this.workingDirectory = workingDirectory;
        this.walletPath = walletPath;
    }

    public RunCommand() {}

    @Override
    public void run() {
        try {
            deploy();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void deploy() throws Exception {
        printlnWithColor("Preparing to run your Web3App", Ansi.Color.GREEN);
        printlnWithColor("Running your Web3App", Ansi.Color.GREEN);
        System.out.print(System.lineSeparator());
        runGradle(workingDirectory);
    }

    private void printlnWithColor(String message, Ansi.Color color) {
        System.out.println(Ansi.ansi().fg(color).a(message).reset());
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
        }
    }

    private void setEnvironment(final ProcessBuilder processBuilder) {
        processBuilder.environment().put(WEB3J_VAR_PREFIX + "NETWORK", nodeURL);
        processBuilder.environment().put(WEB3J_VAR_PREFIX + "NODE_URL", nodeURL);
        processBuilder.environment().put(WEB3J_VAR_PREFIX + "WALLET_PATH", walletPath);
        processBuilder.environment().put(WEB3J_VAR_PREFIX + "WALLET_PASSWORD", walletPassword);
        processBuilder
                .environment()
                .putIfAbsent(WEB3J_OPENAPI_VAR_PREFIX + "PORT", Integer.toString(9090));
    }
}
