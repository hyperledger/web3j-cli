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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import org.web3j.console.project.utills.ClassExecutor;

import static java.io.File.separator;

public class UnitTestCreatorTest extends ClassExecutor {
    private String formattedPath =
            new File(String.join(separator, "src", "test", "resources", "Solidity"))
                    .getAbsolutePath();
    private String tempDirPath;

    @BeforeEach
    void setup(@TempDir Path temp) {
        this.tempDirPath = temp.toString();
    }

    @Test
    public void testThatCorrectArgumentsArePassed() {
        final String[] args = {"-i=" + formattedPath, "-o=" + tempDirPath};
        final UnitTestCLIRunner unitTestCLIRunner = new UnitTestCLIRunner();
        new CommandLine(unitTestCLIRunner).parseArgs(args);
        assert unitTestCLIRunner.unitTestOutputDir.equals(tempDirPath);
        assert unitTestCLIRunner.javaWrapperDir.equals(formattedPath);
    }

    @Test
    public void verifyThatTestsAreGenerated() throws IOException, InterruptedException {
        final String[] args = {
            "-p=org.com", "-n=Testing", "-o=" + tempDirPath, "-s=" + formattedPath
        };
        final String pathToJavaWrappers =
                new File(
                                String.join(
                                        separator,
                                        tempDirPath,
                                        "Testing",
                                        "build",
                                        "generated",
                                        "source",
                                        "web3j",
                                        "main",
                                        "java"))
                        .getCanonicalPath();
        int exitCode =
                executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .inheritIO()
                        .start()
                        .waitFor();
        Assertions.assertEquals(0, exitCode);
        final String[] unitTestsArgs = {"-i=" + pathToJavaWrappers, "-o=" + tempDirPath};
        int testsExitCode =
                executeClassAsSubProcessAndReturnProcess(
                                UnitTestCreator.class,
                                Collections.emptyList(),
                                Arrays.asList(unitTestsArgs))
                        .inheritIO()
                        .start()
                        .waitFor();
        Assertions.assertEquals(0, testsExitCode);
    }
}
