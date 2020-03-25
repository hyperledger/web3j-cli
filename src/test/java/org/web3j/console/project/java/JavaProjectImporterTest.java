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
package org.web3j.console.project.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import org.web3j.console.project.ProjectImporter;
import org.web3j.console.project.utils.ClassExecutor;
import org.web3j.console.project.utils.Folders;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaProjectImporterTest extends ClassExecutor {
    static String tempDirPath;
    private String formattedPath =
            new File(String.join(separator, "src", "test", "resources", "Solidity"))
                    .getAbsolutePath();

    @BeforeAll
    public static void setUpStreams() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
    }

    @Test
    @Order(1)
    public void testWhenCorrectArgsArePassedProjectStructureCreated() {
        final String[] args = {"-p=org.com", "-n=Test", "-o=" + tempDirPath, "-s=" + tempDirPath};
        final JavaProjectImporterCLIRunner projectImporterCLIRunner =
                new JavaProjectImporterCLIRunner();
        new CommandLine(projectImporterCLIRunner).parseArgs(args);
        assertEquals(projectImporterCLIRunner.packageName, "org.com");
        assertEquals(projectImporterCLIRunner.projectName, "Test");
        assertEquals(projectImporterCLIRunner.solidityImportPath, tempDirPath);
    }

    @Test
    @Order(2)
    public void testWithPicoCliWhenArgumentsAreCorrect() throws IOException, InterruptedException {
        final String[] args = {
            "--java", "-p=org.com", "-n=Test5", "-o=" + tempDirPath, "-s=" + formattedPath, "-t"
        };
        int exitCode =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class,
                                Collections.emptyList(),
                                Arrays.asList(args),
                                true)
                        .inheritIO()
                        .start()
                        .waitFor();
        assertEquals(0, exitCode);
    }

    @Test
    @Order(3)
    public void verifyWhenInteractiveThatTestsHaveBeenGenerated() {
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

    @Test
    public void testWithPicoCliWhenArgumentsAreEmpty() throws IOException, InterruptedException {
        final String[] args = {"--java", "-p=", "-n=", "-s="};
        ProcessBuilder pb =
                executeClassAsSubProcessAndReturnProcess(
                        ProjectImporter.class, Collections.emptyList(), Arrays.asList(args), false);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            assertEquals(
                    1L,
                    reader.lines()
                            .filter(
                                    l ->
                                            l.contains(
                                                    "Please make sure the required parameters are not empty."))
                            .count());
        }
        process.waitFor();
    }
}
