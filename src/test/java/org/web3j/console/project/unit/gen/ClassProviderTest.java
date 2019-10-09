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
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.web3j.console.project.ProjectImporter;
import org.web3j.console.project.utills.ClassExecutor;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassProviderTest extends ClassExecutor {
    @TempDir static File temp;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        String formattedPath = "src/test/resources/Solidity".replaceAll("/", File.separator);
        final String[] args = {"import"};
        Process process =
                new ClassExecutor()
                        .executeClassAsSubProcessAndReturnProcess(
                                ProjectImporter.class, Collections.emptyList(), Arrays.asList(args))
                        .start();
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.write("test", 0, "test".length());
        writer.newLine();
        writer.write("org.com", 0, "org.com".length());
        writer.newLine();
        writer.write(formattedPath, 0, formattedPath.length());
        writer.newLine();
        writer.write(temp.getPath(), 0, temp.getPath().length());
        writer.newLine();
        writer.close();
        process.waitFor();
    }

    @Test
    public void getClassesWhenSuccessfulTest() throws IOException, ClassNotFoundException {
        File pathToProject =
                new File(
                        temp
                                + separator
                                + "test"
                                + separator
                                + "build"
                                + separator
                                + "generated"
                                + separator
                                + "source"
                                + separator
                                + "web3j"
                                + separator
                                + "main"
                                + separator
                                + "java");
        ClassProvider classProvider = new ClassProvider(pathToProject);
        assertEquals(4, classProvider.getClasses().size());
    }

    @Test
    public void getClassesWhenNotSuccessfulTest() {
        File pathToProject =
                new File(
                        temp
                                + separator
                                + "test"
                                + separator
                                + "build"
                                + separator
                                + "generated"
                                + separator
                                + "source"
                                + separator
                                + "web3j"
                                + separator);
        ClassProvider classProvider = new ClassProvider(pathToProject);
        assertThrows(ClassNotFoundException.class, () -> classProvider.getClasses());
    }
}
