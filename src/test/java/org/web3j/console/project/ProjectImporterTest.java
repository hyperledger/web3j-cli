/*
 * Copyright 2019 Web3 Labs LTD.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import org.web3j.console.project.utills.ClassExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectImporterTest extends ClassExecutor {

    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    public void setUpStreams(@TempDir Path temp) {
        tempDirPath = temp.toString();
    }

    @Test
    public void testWhenCorrectArgsArePassedProjectStructureCreated() {
        final String[] args = {"-p=org.com", "-n=Test", "-o=" + tempDirPath, "-s=" + tempDirPath};
        final ProjectImporterCLIRunner projectImporterCLIRunner = new ProjectImporterCLIRunner();
        new CommandLine(projectImporterCLIRunner).parseArgs(args);
        assertEquals(projectImporterCLIRunner.packageName, "org.com");
        assertEquals(projectImporterCLIRunner.projectName, "Test");
        assertEquals(projectImporterCLIRunner.solidityImportPath, tempDirPath);
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreCorrect() throws IOException, InterruptedException {

        final String formattedSolidityTestProject =
                File.separator
                        + "web3j"
                        + File.separator
                        + "console"
                        + File.separator
                        + "src"
                        + File.separator
                        + "test"
                        + File.separator
                        + "resources"
                        + File.separator
                        + "Solidity";

        final String[] args = {
            "-p=org.com", "-n=Test", "-o=" + tempDirPath, "-s=" + formattedSolidityTestProject
        };
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        int exitCode = ps.waitFor();
        assertTrue(exitMessage.contains("Project created with name: Test at location: "));
        assertEquals(0, exitCode);
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreEmpty() throws IOException, InterruptedException {
        final String[] args = {"import", "-p= ", "-n= ", "-s= "};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        int exitCode = ps.waitFor();
        assertEquals("Please make sure the required parameters are not empty.", exitMessage);
        assertEquals(1, exitCode);
    }

    @Test
    public void testWhenInteractiveAndArgumentsAreCorrect()
            throws IOException, InterruptedException {
        String formattedPath =
                "/web3j/console/src/test/resources/Solidity".replace("/", File.separator);
        final String[] args = {"import"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(formattedPath, 0, formattedPath.length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        ps.waitFor();
        assertTrue(exitMessage.contains("Project created with name: test at location: "));
        assertEquals(0, ps.exitValue());
    }

    @Test
    public void testWhenInteractiveAndFirstInputIsInvalidClassName()
            throws IOException, InterruptedException {
        String formattedPath =
                "/web3j/console/src/test/resources/Solidity".replace("/", File.separator);
        final String[] args = {"import"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("#$%^%#$test", 0, "#$%^%#$test".length());
        writer.newLine();
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(formattedPath, 0, formattedPath.length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        ps.waitFor();
        assertTrue(exitMessage.contains("#$%^%#$test is a not valid name."));
        assertEquals(0, ps.exitValue());
    }

    @Test
    public void testWhenInteractiveAndFirstInputIsInvalidPackageName()
            throws IOException, InterruptedException {
        String formattedPath =
                "/web3j/console/src/test/resources/Solidity".replace("/", File.separator);
        final String[] args = {"import"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("@#@$%@%@$#@org.com", 0, "@#@$%@%@$#@org.com".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(formattedPath, 0, formattedPath.length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        ps.waitFor();
        assertTrue(exitMessage.contains("@#@$%@%@$#@org.com is not a valid package name."));
        assertEquals(0, ps.exitValue());
    }

    @Test
    public void testWhenInteractiveAndArgumentsAreEmpty() {
        final String input = " \n \n \n \n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        final String[] args = {"import"};
        assertThrows(NoSuchElementException.class, () -> ProjectImporter.main(args));
    }
}
