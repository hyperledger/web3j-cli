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
package org.web3j.console;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import org.web3j.console.project.UnitTestCommand;
import org.web3j.console.wrapper.subcommand.SolidityGenerateCommand;
import org.web3j.console.wrapper.subcommand.TruffleGenerateCommand;

@Command(
        name = "generate",
        subcommands = {
            CommandLine.HelpCommand.class,
            SolidityGenerateCommand.class,
            TruffleGenerateCommand.class,
            UnitTestCommand.class,
        },
        showDefaultValues = true,
        abbreviateSynopsis = true,
        description = "Generate Wrappers or Tests",
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class GenerateCommand extends SubCommand {}
