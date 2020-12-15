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
package org.web3j.console.project;

import picocli.CommandLine;

import org.web3j.console.SubCommand;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.project.java.JavaTestCLIRunner;
import org.web3j.console.project.kotlin.KotlinTestCLIRunner;

@CommandLine.Command(
        name = "tests",
        description = "Generate tests for a Web3j Java smart contract wrapper",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        subcommands = {JavaTestCLIRunner.class, KotlinTestCLIRunner.class},
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class UnitTestCommand extends SubCommand {}
