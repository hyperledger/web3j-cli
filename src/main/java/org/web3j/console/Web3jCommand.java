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
package org.web3j.console;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.web3j.codegen.Console;
import org.web3j.console.config.ConfigManager;
import org.web3j.console.docker.DockerCommand;
import org.web3j.console.openapi.OpenApiCommand;
import org.web3j.console.project.ImportProjectCommand;
import org.web3j.console.project.NewProjectCommand;
import org.web3j.console.project.testing.ProjectTestCommand;
import org.web3j.console.project.utils.InstructionsPrinter;
import org.web3j.console.project.utils.printer.Web3jPrinter;
import org.web3j.console.run.RunCommand;
import org.web3j.console.security.ContractAuditCommand;
import org.web3j.console.services.Telemetry;
import org.web3j.console.services.Updater;
import org.web3j.console.wallet.WalletCommand;

import static java.io.File.separator;
import static org.web3j.codegen.Console.exitSuccess;
import static org.web3j.console.config.ConfigManager.config;

/** Main entry point for running command line utilities. */
@Command(
        name = "web3j",
        subcommands = {
            RunCommand.class,
            ContractAuditCommand.class,
            GenerateCommand.class,
            CommandLine.HelpCommand.class,
            ImportProjectCommand.class,
            NewProjectCommand.class,
            OpenApiCommand.class,
            ProjectTestCommand.class,
            DockerCommand.class,
            WalletCommand.class,
        },
        showDefaultValues = true,
        abbreviateSynopsis = true,
        description = "Run Web3j CLI commands",
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class Web3jCommand implements Runnable {

    public static final String DEFAULT_WALLET_FOLDER =
            System.getProperty("user.home") + separator + ".web3j" + separator + "keystore";

    private static final String LOGO =
            // generated at http://patorjk.com/software/taag
            "              _      _____ _ \n"
                    + "             | |    |____ (_)\n"
                    + "__      _____| |__      / /_ \n"
                    + "\\ \\ /\\ / / _ \\ '_ \\     \\ \\ |\n"
                    + " \\ V  V /  __/ |_) |.___/ / |\n"
                    + "  \\_/\\_/ \\___|_.__/ \\____/| |\n"
                    + "                         _/ |\n"
                    + "                        |__/ "
                    + "\nby Web3Labs";

    private final CommandLine commandLine;
    private final Map<String, String> environment;
    private final String[] args;

    @Option(
            names = {"--telemetry"},
            description = "Whether to perform analytics.",
            defaultValue = "false")
    public boolean telemetry;

    public Web3jCommand(final Map<String, String> environment, String[] args) {
        this.commandLine = new CommandLine(this);
        this.environment = environment;
        this.args = args;
        InstructionsPrinter.initContextPrinter(new Web3jPrinter());
    }

    public int parse() {
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setParameterExceptionHandler(this::handleParseException);
        commandLine.setDefaultValueProvider(new EnvironmentVariableDefaultProvider(environment));
        System.out.println(LOGO);
        try {
            ConfigManager.setProduction();
            Updater.promptIfUpdateAvailable();
        } catch (IOException e) {
            Console.exitError("Failed to initialise the CLI");
        }

        return commandLine.execute(args);
    }

    private int handleParseException(final CommandLine.ParameterException ex, final String[] args) {
        commandLine.getErr().println(ex.getMessage());

        CommandLine.UnmatchedArgumentException.printSuggestions(ex, commandLine.getOut());
        commandLine.usage(commandLine.getOut());

        return ex.getCommandLine().getCommandSpec().exitCodeOnInvalidInput();
    }

    @Override
    public void run() {
        performTelemetryUpload();
    }

    private void performTelemetryUpload() {
        if (args.length == 0) {
            commandLine.usage(commandLine.getOut());
        }
        if (telemetry) {
            Telemetry.uploadTelemetry(args);
            Updater.onlineUpdateCheck();
            exitSuccess();
        } else if (!config.isTelemetryDisabled()) {
            try {
                Telemetry.invokeTelemetryUpload(args);
            } catch (URISyntaxException | IOException e) {
                Console.exitError("Failed to invoke telemetry upload");
            }
        }
    }
}
