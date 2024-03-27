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
package org.web3j.console.utils;

import org.fusesource.jansi.Ansi;

public class PrinterUtilities {

    public static void printErrorAndExit(String errorMessage) {
        System.out.println(Ansi.ansi().fgBrightRed().bold().a(errorMessage).reset());
        System.exit(1);
    }

    public static void printInformationPair(
            String firstText, int leftJustify, String secondText, Ansi.Color informationColor) {
        System.out.print(
                Ansi.ansi()
                        .fgBrightDefault()
                        .a(String.format("%-" + leftJustify + "s", firstText))
                        .reset());
        System.out.println(Ansi.ansi().fg(informationColor).a(secondText).reset());
    }

    public static void printInformationPairWithStatus(
            String firstText, int leftJustify, String secondText, Ansi.Color statusTextColor) {
        System.out.print(
                Ansi.ansi()
                        .eraseLine()
                        .fgBrightDefault()
                        .a(String.format("%-" + leftJustify + "s", firstText))
                        .reset());
        System.out.println(Ansi.ansi().fg(statusTextColor).a(secondText).reset());
    }

    public static void main(String[] args) {
        // Example usage
        printErrorAndExit("Error message with exit.");
        printInformationPair("Info:", 10, "This is an information message.", Ansi.Color.CYAN);
        printInformationPairWithStatus(
                "Status:", 10, "This is a status message.", Ansi.Color.GREEN);
    }
}
