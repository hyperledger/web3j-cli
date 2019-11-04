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

public class ProjectCreatorTest extends ClassExecutor {

    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    public void setUpStreams(@TempDir Path temp) {

        tempDirPath = temp.toString();
    }

    @Test
    public void testWhenCorrectArgsArePassedProjectStructureCreated() {
        final String[] args = {"-p=org.com", "-n=Test", "-o=" + tempDirPath};
        final ProjectCreatorCLIRunner projectCreatorCLIRunner = new ProjectCreatorCLIRunner();
        new CommandLine(projectCreatorCLIRunner).parseArgs(args);
        assert projectCreatorCLIRunner.packageName.equals("org.com");
        assert projectCreatorCLIRunner.projectName.equals("Test");
        assert projectCreatorCLIRunner.outputDir.equals(tempDirPath);
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreCorrect() throws IOException, InterruptedException {
        final String[] args = {"new", "-p", "org.com", "-n", "Test", "-o" + tempDirPath};
        int exitCode =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .inheritIO()
                        .start()
                        .waitFor();
        assertEquals(0, exitCode);
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreEmpty() throws IOException, InterruptedException {
        final String[] args = {"new", "-n= ", "-p= "};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
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
        final String[] args = {"new"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String exitMessage = in.lines().collect(Collectors.joining());
        int exitCode = ps.waitFor();
        ps.waitFor();
        assertTrue(exitMessage.contains("Project created with name: test at location: "));
        assertEquals(0, exitCode);
    }

    @Test
    public void testWhenInteractiveAndFirstInputIsInvalidClassName()
            throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("#$%^%#$test", 0, "#$%^%#$test".length());
        writer.newLine();
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
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
        final String[] args = {"new"};
        Process ps =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("@#@$%@%@$#@org.com", 0, "@#@$%@%@$#@org.com".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
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
        final String input = " \n \n \n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        final String[] args = {"new"};

        assertThrows(NoSuchElementException.class, () -> ProjectCreator.main(args));
    }
}
