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

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.docker.DockerOperations;

@Command(
        name = "build",
        description = "Build project in docker",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class DockerBuildCommand implements DockerOperations, Runnable {

    @Option(names = {"-d", "--directory"})
    Path directory = Paths.get(System.getProperty("user.dir"));

    @Option(
            names = {"-t", "--tag"},
            description = {"specify the tag for the docker image."},
            defaultValue = "web3app")
    String tag = "web3app";

    public void run() {
        try {
            executeDocker(new String[] {"docker", "build", "-t", tag, "."}, directory);
        } catch (Exception e) {
            Console.exitError(e);
        }
    }
}
