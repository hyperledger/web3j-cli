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
package org.web3j.console.docker;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import picocli.CommandLine;

import org.web3j.console.ProjectTest;
import org.web3j.console.Web3jCommand;
import org.web3j.console.config.ConfigManager;
import org.web3j.console.project.InteractiveOptions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DockerCommandTest extends ProjectTest {

    @BeforeEach
    public void setupWallet() {

        final String walletPath =
                new InteractiveOptions().createWallet(Web3jCommand.DEFAULT_WALLET_FOLDER, "");
        ConfigManager.setDevelopment("", "", "", walletPath, "", true);
    }

    @Disabled("until the web3j-cli init release")
    @Test
    @Order(1)
    public void testDockerBuild() {
        new CommandLine(new DockerCommand())
                .execute(
                        "build",
                        "-d",
                        Paths.get(workingDirectory.getAbsolutePath(), "Test").toString());
    }

    @Disabled("until the web3j-cli init release")
    @Test
    @Order(2)
    public void testDockerRun() {
        new CommandLine(new DockerCommand()).execute("run", "-w", System.getProperty("user.home"));
    }
}
