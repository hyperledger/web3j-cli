/*
 * Copyright 2021 Web3 Labs Ltd.
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
package org.web3j.console.deploy;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;

import static org.web3j.console.utils.PrinterUtilities.coloredPrinter;

@Command(
        name = "deploy",
        description = "Deploy smart contracts on the given network",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class DeployCommand implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "profile-name",
            description = "Profile name for which deployable is formed",
            arity = "1")
    String profileName;

    @Parameters(
            index = "1",
            paramLabel = "package-name",
            description = "Package name which contains the deployable and ordering",
            arity = "1")
    String packageName;
    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread,
     * starting the thread causes the object's <code>run</code> method to be called in that
     * separately executing thread.
     *
     * <p>The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        coloredPrinter.println("Starting to deploy" + profileName + " " + packageName);
    }
}
