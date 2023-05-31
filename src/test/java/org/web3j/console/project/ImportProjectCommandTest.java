/*
 * Copyright 2019 Web3 Labs Ltd.
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
package org.web3j.console.project;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import org.web3j.console.project.utils.ClassExecutor;
import org.web3j.console.project.utils.Folders;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportProjectCommandTest extends ClassExecutor {

    private static String tempDirPath;

    private final String solidityTestDir =
            Paths.get("src", "test", "resources", "Solidity").toFile().getAbsolutePath();

    @BeforeAll
    public static void setUpStreams() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
    }

    @Test
    public void testWhenCorrectArgsArePassedProjectStructureCreated() {
        final String[] args = {
            "-p=org.com", "-n=Test", "-o=" + tempDirPath, "-s=" + solidityTestDir
        };
        final ImportProjectCommand importProjectCommand = new ImportProjectCommand();
        new CommandLine(importProjectCommand).parseArgs(args);
        assertEquals("Test", importProjectCommand.projectOptions.projectName);
        assertEquals("org.com", importProjectCommand.projectOptions.packageName);
        assertEquals(tempDirPath, importProjectCommand.projectOptions.outputDir);
        assertEquals(solidityTestDir, importProjectCommand.solidityImportPath);
    }

    @Test
    public void testWhenNonDefinedArgsArePassed() {
        final ImportProjectCommand importProjectCommand = new ImportProjectCommand();
        final String[] args = {"-t=org.org", "-b=test", "-z=" + tempDirPath};
        final CommandLine commandLine = new CommandLine(importProjectCommand);
        Assertions.assertThrows(
                CommandLine.ParameterException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testWhenDuplicateArgsArePassed() {
        final ImportProjectCommand importProjectCommand = new ImportProjectCommand();
        final String[] args = {
            "-p=org.org", "-n=test", "-n=OverrideTest", "-o=" + tempDirPath, "-s=test"
        };
        final CommandLine commandLine = new CommandLine(importProjectCommand);
        Assertions.assertThrows(
                CommandLine.OverwrittenOptionException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreCorrect() {
        final String[] args = {
            "-p", "org.com", "-n", "Test5", "-o", tempDirPath, "-s", solidityTestDir
        };
        PrintWriter outFile = new PrintWriter(System.out);
        int exitCode = new CommandLine(ImportProjectCommand.class).setErr(outFile).execute(args);
        //        int exitCode = new CommandLine(ImportProjectCommand.class).execute(args);
        assertEquals(0, exitCode);

        String pathToTests =
                String.join(
                        separator,
                        tempDirPath,
                        "Test5",
                        "src",
                        "test",
                        "java",
                        "org",
                        "com",
                        "generated",
                        "contracts",
                        "Test2Test.java");
        assertTrue(new File(pathToTests).exists());
    }
}
