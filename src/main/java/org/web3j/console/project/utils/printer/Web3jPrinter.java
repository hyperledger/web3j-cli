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
package org.web3j.console.project.utils.printer;

import org.fusesource.jansi.Ansi;

public class Web3jPrinter extends Printer {

    @Override
    public void printInstructionsOnSuccess() {
        String gradleCommand =
                System.getProperty("os.name").toLowerCase().startsWith("windows")
                        ? "./gradlew.bat"
                        : "./gradlew";

        System.out.println(Ansi.ansi().fgYellow().bold().a("Commands").reset());
        System.out.println(
                String.format("%-40s", gradleCommand + " test") + "Test your application");
        System.out.println(String.format("%-40s", "web3j run") + "Runs your application");
        System.out.println(
                String.format("%-40s", "web3j docker run") + "Runs your application in docker");
    }

    @Override
    public void printInstructionsOnSuccessOpenApi() {
        String gradleCommand =
                System.getProperty("os.name").toLowerCase().startsWith("windows")
                        ? "./gradlew.bat"
                        : "./gradlew";

        System.out.println();
        System.out.println(Ansi.ansi().fgGreen().a("Project Created Successfully").reset());
        System.out.println();

        System.out.println(Ansi.ansi().fgYellow().bold().a("Commands").reset());
        System.out.println(
                String.format("%-40s", gradleCommand + " run") + "Run your application manually");
    }
}
