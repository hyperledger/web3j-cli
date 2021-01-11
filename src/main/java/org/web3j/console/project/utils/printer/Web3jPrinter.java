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

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

public class Web3jPrinter extends Printer {

    @Override
    public void printInstructionsOnSuccess(
            ColoredPrinter instructionPrinter, ColoredPrinter commandPrinter) {
        String gradleCommand =
                System.getProperty("os.name").toLowerCase().startsWith("windows")
                        ? "./gradlew.bat"
                        : "./gradlew";
        instructionPrinter.println(
                "Commands", Ansi.Attribute.LIGHT, Ansi.FColor.YELLOW, Ansi.BColor.BLACK);
        instructionPrinter.print(String.format("%-40s", gradleCommand + " test"));
        commandPrinter.println("Test your application");
        instructionPrinter.print(String.format("%-40s", "web3j run"));
        commandPrinter.print("Runs your application\n");
        instructionPrinter.print(String.format("%-40s", "web3j docker run"));
        commandPrinter.print("Runs your application in docker");
    }

    @Override
    public void printInstructionsOnSuccessOpenApi(
            ColoredPrinter instructionPrinter, ColoredPrinter commandPrinter) {
        String gradleCommand =
                System.getProperty("os.name").toLowerCase().startsWith("windows")
                        ? "./gradlew.bat"
                        : "./gradlew";
        System.out.println(System.lineSeparator());
        commandPrinter.println("Project Created Successfully");
        System.out.println(System.lineSeparator());

        instructionPrinter.println(
                "Commands", Ansi.Attribute.LIGHT, Ansi.FColor.YELLOW, Ansi.BColor.BLACK);
        instructionPrinter.print(String.format("%-40s", gradleCommand + " run"));
        commandPrinter.println("Run your application manually");
    }
}
