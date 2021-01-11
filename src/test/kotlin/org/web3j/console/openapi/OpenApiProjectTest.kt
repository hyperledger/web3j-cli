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
package org.web3j.console.openapi

import org.web3j.console.openapi.subcommands.GenerateOpenApiCommand
import org.web3j.console.openapi.subcommands.ImportOpenApiCommand
import org.web3j.console.openapi.subcommands.JarOpenApiCommand
import org.web3j.console.openapi.subcommands.NewOpenApiCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.web3j.console.project.utils.Folders
import picocli.CommandLine
import java.io.File
import java.nio.file.Paths

class OpenApiProjectTest {

    private val tempDirPath = Folders.tempBuildFolder().absolutePath
    private val soliditySource: File = Paths.get("src", "test", "resources", "Solidity", "TestContract.sol").toFile()

    @Test
    fun testCorrectArgsOpenApiEndpointsGeneration() {
        val args = arrayOf("-o", tempDirPath, "-n", "generationTest", "-s", soliditySource.absolutePath)
        val exitCode = CommandLine(GenerateOpenApiCommand::class.java).execute(*args)
        assertEquals(0, exitCode)
    }

    @Test
    fun testCorrectArgsOpenApiJarGeneration() {
        val args = arrayOf("-p", "org.com", "-n", "Test", "-o", tempDirPath, "-s", soliditySource.absolutePath)
        val exitCode = CommandLine(JarOpenApiCommand::class.java).execute(*args)
        assertEquals(0, exitCode)
        val jarFile = Paths.get(tempDirPath, "Test-server-all.jar").toFile()
        assertTrue(jarFile.exists())
    }

    @Test
    fun testCorrectArgsOpenApiNew() {
        val args = arrayOf("-p", "org.com", "-n", "Test", "-o", tempDirPath)
        val exitCode = CommandLine(NewOpenApiCommand::class.java).execute(*args)
        assertEquals(0, exitCode)
    }

    @Test
    fun testCorrectArgsOpenApiNewErc20() {
        val args = arrayOf("ERC20", "-p", "org.com", "-n", "Test", "-o", tempDirPath)
        val exitCode = CommandLine(NewOpenApiCommand::class.java).execute(*args)
        assertEquals(0, exitCode)
    }

    @Test
    fun testCorrectArgsOpenApiNewErc777() {
        val args = arrayOf("ERC777", "-p", "org.com", "-n", "Test", "-o", tempDirPath)
        val exitCode = CommandLine(NewOpenApiCommand::class.java).execute(*args)
        assertEquals(0, exitCode)
    }

    @Test
    fun testCorrectArgsOpenApiImport() {
        val args = arrayOf("-p", "org.com", "-n", "Test", "-o", tempDirPath, "-s", soliditySource.absolutePath)
        val exitCode = CommandLine(ImportOpenApiCommand()).execute(*args)
        assertEquals(0, exitCode)
    }
}
