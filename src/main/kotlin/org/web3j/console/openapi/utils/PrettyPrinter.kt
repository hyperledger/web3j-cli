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

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi

object PrettyPrinter {
    private val gradleCommand = if (System.getProperty("os.name").toLowerCase().startsWith("windows")) "./gradlew.bat" else "./gradlew"

    private val cp = ColoredPrinter.Builder(0, false)
        .foreground(Ansi.FColor.WHITE)
        .background(Ansi.BColor.GREEN)
        .attribute(Ansi.Attribute.BOLD)
        .build()
    private val cpf = ColoredPrinter.Builder(0, false)
        .foreground(Ansi.FColor.RED)
        .background(Ansi.BColor.YELLOW)
        .attribute(Ansi.Attribute.BOLD)
        .build()
    private val instructionPrinter = ColoredPrinter.Builder(0, false).foreground(Ansi.FColor.CYAN).build()
    private val commandPrinter = ColoredPrinter.Builder(0, false).foreground(Ansi.FColor.GREEN).build()

    fun onOpenApiProjectSuccess() {
        print(System.lineSeparator())
        cp.println("Project Created Successfully")
        print(System.lineSeparator())

        instructionPrinter.println(
            "Commands", Ansi.Attribute.LIGHT, Ansi.FColor.YELLOW, Ansi.BColor.BLACK)
        instructionPrinter.print(String.format("%-40s", "$gradleCommand run"))
        commandPrinter.println("Run your application manually")
        instructionPrinter.print(String.format("%-40s", "epirus run rinkeby|ropsten"))
        commandPrinter.println("Runs your application")
        instructionPrinter.print(String.format("%-40s", "epirus docker run rinkeby|ropsten"))
        commandPrinter.println("Runs your application in a docker container")
    }

    fun onJarSuccess() {
        print(System.lineSeparator())
        cp.println("JAR generated Successfully")
        print(System.lineSeparator())

        instructionPrinter.println(
            "Commands", Ansi.Attribute.LIGHT, Ansi.FColor.YELLOW, Ansi.BColor.BLACK)
        instructionPrinter.print(String.format("%-45s", "java -jar <jar_name> <args>"))
        commandPrinter.println("Run your Jar")
        instructionPrinter.print(String.format("%-45s", "java -jar <jar_name> --help"))
        commandPrinter.println("See the available options")
    }

    fun onSuccess() {
        print(System.lineSeparator())
        cp.println("Project generated Successfully")
        print(System.lineSeparator())
    }

    fun onFailed() {
        print(System.lineSeparator())
        cpf.println("Project generation Failed. Check log file for more information.")
        print(System.lineSeparator())
    }

    fun onWrongPath() {
        print(System.lineSeparator())
        cpf.println("No Solidity smart contracts found! Please enter a correct path containing Solidity code.")
        print(System.lineSeparator())
    }
}
