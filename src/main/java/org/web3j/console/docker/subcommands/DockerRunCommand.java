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
package org.web3j.console.docker.subcommands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.docker.DockerOperations;
import org.web3j.console.project.InteractiveOptions;
import org.web3j.console.wrapper.CredentialsOptions;

import static org.web3j.console.EnvironmentVariablesProperties.*;
import static org.web3j.console.config.ConfigManager.config;
import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(
        name = "run",
        description = "Run project in docker",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class DockerRunCommand implements DockerOperations, Runnable {

    @Option(names = {"-t", "--tag"})
    String tag = "web3app";

    @Parameters(
            index = "0",
            paramLabel = "network",
            description = "Ethereum network [rinkeby/kovan]",
            arity = "1")
    String deployNetwork;

    @Option(names = {"-l", "--local"})
    boolean localMode;

    @Mixin CredentialsOptions credentialsOptions;

    @Option(
            names = {"-d", "--directory"},
            description = "Directory to run Docker in.",
            showDefaultValue = ALWAYS)
    Path directory = Paths.get(System.getProperty("user.dir"));

    @Option(names = {"-p", "--print"})
    boolean print;

    @Override
    public void run() {

        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd().withShowAll(true);

        if (listImagesCmd.exec().stream()
                .flatMap(i -> Arrays.stream(i.getRepoTags()))
                .noneMatch(j -> j.startsWith(tag))) {
            if (new InteractiveOptions()
                    .userAnsweredYes(
                            "It seems that no Docker container has yet been built. Would you like to build a Dockerized version of your app now?")) {
                try {
                    executeDocker(
                            new String[] {"docker", "build", "-t", tag, "."},
                            Paths.get(System.getProperty("user.dir")).toAbsolutePath());
                } catch (Exception e) {
                    Console.exitError(e);
                }
            }
        }

        String[] args =
                new String[] {
                    "docker",
                    "run",
                    "--env",
                    "--env",
                    String.format(WEB3J_CLI_VAR_PREFIX + "DEPLOY=%s", true)
                };

        args = setCredentials(args);
        args = setOpenAPIEnvironment(args);

        if (localMode) {
            args =
                    ArrayUtils.addAll(
                            args,
                            "-v",
                            String.format(
                                    "%s/.web3j:/root/.web3j", System.getProperty("user.home")));
        }

        args = ArrayUtils.addAll(args, tag);

        if (print) {
            System.out.println(String.join(" ", args));
            return;
        }

        try {
            executeDocker(args, directory);
        } catch (Exception e) {
            Console.exitError(e);
        }
    }

    private String[] setOpenAPIEnvironment(final String[] args) {
        return ArrayUtils.addAll(
                args,
                "--env",
                String.format(WEB3J_OPENAPI_VAR_PREFIX + "HOST=%s", "0.0.0.0"),
                "--env",
                String.format(WEB3J_VAR_PREFIX + "NETWORK=%s", deployNetwork),
                "--env",
                String.format(WEB3J_OPENAPI_VAR_PREFIX + "PORT=%d", 9090),
                "-p",
                9090 + ":" + 9090);
    }

    private String[] setCredentials(final String[] args) {
        if (credentialsOptions.getWalletPath() != null) {
            return getWalletEnvironment(
                    args,
                    credentialsOptions.getWalletPath(),
                    credentialsOptions.getWalletPassword());
        } else if (!credentialsOptions.getRawKey().isEmpty()) {
            return ArrayUtils.addAll(
                    args,
                    "--env",
                    String.format(
                            WEB3J_VAR_PREFIX + "PRIVATE_KEY=%s", credentialsOptions.getRawKey()));
        } else if (!credentialsOptions.getJson().isEmpty()) {
            return ArrayUtils.addAll(
                    args,
                    "--env",
                    String.format(
                            WEB3J_VAR_PREFIX + "WALLET_JSON=%s", credentialsOptions.getJson()));
        }
        return getWalletEnvironment(
                args, Paths.get(config.getDefaultWalletPath()), config.getDefaultWalletPassword());
    }

    private String[] getWalletEnvironment(
            final String[] args, final Path walletPath, final String walletPassword) {
        final List<String> strings = Arrays.asList(args);
        final String[] walletArgs =
                ArrayUtils.addAll(
                        args,
                        "--env",
                        String.format(
                                WEB3J_VAR_PREFIX + "WALLET_PATH=%s",
                                "/root/key/" + walletPath.getFileName().toString()),
                        "-v",
                        walletPath.getParent().toAbsolutePath().toString() + ":/root/key");

        if (!walletPassword.isEmpty()) {
            return ArrayUtils.addAll(
                    walletArgs,
                    "--env",
                    String.format(WEB3J_VAR_PREFIX + "WALLET_PASSWORD=%s", walletPassword));
        }
        return strings.toArray(new String[] {});
    }
}
