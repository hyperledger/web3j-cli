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
package org.web3j.console.account.subcommands;

import picocli.CommandLine.Command;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.account.AccountService;
import org.web3j.console.project.InteractiveOptions;

@Command(
        name = "create",
        description = "Create an Epirus account",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Epirus CLI is licensed under the Apache License 2.0")
public class CreateCommand implements Runnable {
    @Override
    public void run() {
        String email = new InteractiveOptions().getEmail();
        AccountService accountService = new AccountService();
        if (accountService.createAccount(email)) {
            System.out.println(
                    "Account created successfully. You can now use Epirus Cloud. Please confirm your e-mail within 24 hours to continue using all features without interruption.");
        } else {
            Console.exitError(
                    "Server response did not contain the authentication token required to create an account.");
        }
    }
}
