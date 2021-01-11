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

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.docker.DockerOperations;
import org.web3j.console.project.InteractiveOptions;

import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_OPENAPI_VAR_PREFIX;
import static org.web3j.console.EnvironmentVariablesProperties.WEB3J_VAR_PREFIX;
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

    @Option(names = {"-l", "--local"})
    boolean localMode;

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

        String[] args = new String[] {"docker", "run"};
        args = setupEnvironmentalVariables(args, Paths.get(walletPath), walletPassword);
        args = setOpenAPIEnvironment(args);
        if (localMode) {
            args =
                    ArrayUtils.addAll(
                            args,
                            "-v",
                            "/Users/alexr/Documents/cli/web3j-cli/build/install/web3j:/root/.web3j");
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
                String.format(WEB3J_VAR_PREFIX + "NETWORK=%s", nodeURL),
                "--env",
                String.format(WEB3J_OPENAPI_VAR_PREFIX + "PORT=%d", 9090),
                "-p",
                9090 + ":" + 9090);
    }

    private String[] setupEnvironmentalVariables(
            final String[] args, final Path walletPath, final String walletPassword) {
        if (walletPassword.isEmpty()) {
            return ArrayUtils.addAll(
                    args,
                    "--env",
                    WEB3J_VAR_PREFIX + "NODE_URL=" + nodeURL,
                    "--env",
                    WEB3J_VAR_PREFIX
                            + "WALLET_PATH="
                            + "/root/key/"
                            + walletPath.getFileName().toString(),
                    "--env",
                    WEB3J_VAR_PREFIX + "WALLET_PASSWORD=" + "''",
                    "-v",
                    walletPath.getParent().toAbsolutePath().toString() + ":/root/key");
        }

        return ArrayUtils.addAll(
                args,
                "--env",
                WEB3J_VAR_PREFIX + "NODE_URL=" + nodeURL,
                "--env",
                WEB3J_VAR_PREFIX
                        + "WALLET_PATH="
                        + "/root/key/"
                        + walletPath.getFileName().toString(),
                "--env",
                WEB3J_VAR_PREFIX + "WALLET_PASSWORD=" + walletPassword,
                "-v",
                walletPath.getParent().toAbsolutePath().toString() + ":/root/key");
    }
}
