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
package org.web3j.console.openapi.utils

import org.web3j.console.project.utils.InstructionsPrinter

import org.fusesource.jansi.Ansi

object PrettyPrinter {

    fun onOpenApiProjectSuccess() {
        InstructionsPrinter.initContextPrinter(null)
        InstructionsPrinter.getContextPrinterInstance().contextPrinter.printInstructionsOnSuccessOpenApi()
    }

    fun onJarSuccess() {
        println(System.lineSeparator())
        println(Ansi.ansi().fgGreen().bold().a("JAR generated Successfully").reset())
        println(System.lineSeparator())

        println(Ansi.ansi().fgCyan().a("Commands").reset())
        println(String.format("%-45s", "java -jar <jar_name> <args>") + Ansi.ansi().fgGreen().a(" Run your Jar").reset())
        println(String.format("%-45s", "java -jar <jar_name> --help") + Ansi.ansi().fgGreen().a(" See the available options").reset())
    }

    fun onSuccess() {
        println(System.lineSeparator())
        println(Ansi.ansi().fgGreen().bold().a("Project generated Successfully").reset())
        println(System.lineSeparator())
    }

    fun onFailed() {
        println(System.lineSeparator())
        println(Ansi.ansi().fgRed().bold().a("Project generation Failed. Check log file for more information.").reset())
        println(System.lineSeparator())
    }

    fun onWrongPath() {
        println(System.lineSeparator())
        println(Ansi.ansi().fgRed().bold().a("No Solidity smart contracts found! Please enter a correct path containing Solidity code.").reset())
        println(System.lineSeparator())
    }
}
