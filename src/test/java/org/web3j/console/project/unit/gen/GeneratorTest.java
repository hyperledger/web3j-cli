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
package org.web3j.console.project.unit.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.web3j.console.project.ProjectCreator;
import org.web3j.console.project.utills.ClassExecutor;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class GeneratorTest extends ClassExecutor {


    File temp = Files.createTempDir();

    @TempDir
    static Path tempDir;

    @Test
    public void testThatUnitClassWasGenerated() throws IOException, InterruptedException {
        final String[] args = {"new"};
        Process process =
                executeClassAsSubProcessAndReturnProcess(
                        ProjectCreator.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("Test", 0, "Test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(temp.getPath(), 0, temp.getPath().length());
        writer.newLine();
        writer.close();
        process.waitFor();

        String[] genArgs = {"generate", temp + File.separator + "Test"};
        Generator.main(genArgs);
        System.out.println(Arrays.toString(new File(temp
                + File.separator
                + "Test"
                + File.separator
                + "src"
                + File.separator
                + "test"
                + File.separator
                + "solidity").list()));
        assertTrue(
                new File(
                        temp
                                        + File.separator
                                        + "Test"
                                        + File.separator
                                        + "src"
                                        + File.separator
                                        + "test"
                                        + File.separator
                                        + "solidity"
                                        + File.separator
                                        + "org"
                                        + File.separator
                                        + "com"
                                        + File.separator
                                        + "Greeter.java").exists()
        );
    }
}
