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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import org.web3j.console.project.utils.ClassExecutor;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectCreatorTest extends ClassExecutor {
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    public void setUpStreams(@TempDir Path temp) {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
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
    public void verifyThatTestsAreGenerated() throws IOException, InterruptedException {
        final String[] args = {"new", "-p", "org.com", "-n", "Test", "-o" + tempDirPath};
        final File pathToTests =
                new File(
                        String.join(
                                separator,
                                tempDirPath,
                                "Test",
                                "src",
                                "test",
                                "java",
                                "org",
                                "com",
                                "generated",
                                "contracts",
                                "HelloWorldTest.java"));
        int exitCode =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .inheritIO()
                        .start()
                        .waitFor();
        assertEquals(0, exitCode);
        assertTrue(pathToTests.exists());
    }

    @Test
    public void testWithPicoCliWhenArgumentsAreEmpty() {
        final String[] args = {"new", "-n=", "-p="};
        ProjectCreator.main(args);
        assertEquals(
                outContent.toString(), "Please make sure the required parameters are not empty.\n");
    }

    @Test
    public void testWhenInteractiveAndArgumentsAreCorrect()
            throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testWhenInteractiveAndFirstInputIsInvalidClassName()
            throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("#$%^%#$test", 0, "#$%^%#$test".length());
        writer.newLine();
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testWhenInteractiveAndFirstInputIsInvalidPackageName()
            throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("@#@$%@%@$#@org.com", 0, "@#@$%@%@$#@org.com".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.close();
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    @Test
    public void testWhenInteractiveAndOptionsAreDefault() throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("", 0, 0);
        writer.newLine();
        writer.write("", 0, 0);
        writer.newLine();
        writer.write(tempDirPath, 0, tempDirPath.length());
        writer.newLine();
        writer.write("y", 0, "y".length());
        writer.newLine();
        writer.close();
        process.waitFor();
        assertEquals(0, process.exitValue());
        assertTrue(new File(tempDirPath + separator + "BlockchainApp").exists());
    }
}
