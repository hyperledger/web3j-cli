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

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

public class PrinterUtilities {
    private static final String CARRIAGE_RETURN = "\r";
    public static ColoredPrinter coloredPrinter =
            new ColoredPrinter.Builder(0, false)
                    .foreground(Ansi.FColor.WHITE)
                    .background(Ansi.BColor.GREEN)
                    .attribute(Ansi.Attribute.BOLD)
                    .build();

    public static void printErrorAndExit(String errorMessage) {
        coloredPrinter.println(
                errorMessage, Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        System.exit(1);
    }

    public static void printInformationPair(
            String firstText, int leftJustify, String secondText, Ansi.FColor informationColor) {
        coloredPrinter.print(
                String.format("%-" + leftJustify + "s", firstText),
                Ansi.Attribute.CLEAR,
                Ansi.FColor.WHITE,
                Ansi.BColor.BLACK);
        coloredPrinter.println(
                secondText, Ansi.Attribute.CLEAR, informationColor, Ansi.BColor.BLACK);
    }

    public static void printInformationPairWithStatus(
            String firstText, int leftJustify, String secondText, Ansi.FColor statusTextColor) {
        System.out.print(CARRIAGE_RETURN);
        coloredPrinter.print(
                String.format("%-" + leftJustify + "s", firstText),
                Ansi.Attribute.CLEAR,
                Ansi.FColor.WHITE,
                Ansi.BColor.BLACK);
        coloredPrinter.print(secondText, Ansi.Attribute.CLEAR, statusTextColor, Ansi.BColor.BLACK);
    }
}
