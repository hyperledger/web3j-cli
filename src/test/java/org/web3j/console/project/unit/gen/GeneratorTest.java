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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import kotlin.text.Charsets;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import org.web3j.console.project.utills.ClassExecutor;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest extends Setup {

    @Test
    public void testThatUnitClassWasGenerated() {
        System.out.println(
                Arrays.toString(
                        new File(
                                        temp
                                                + separator
                                                + "test"
                                                + separator
                                                + "src"
                                                + separator
                                                + "test"
                                                + separator
                                                + "solidity"
                                                + separator
                                                + "org"
                                                + separator
                                                + "com"
                                                + separator)
                                .listFiles()));
        assertTrue(
                new File(
                                temp
                                        + separator
                                        + "test"
                                        + separator
                                        + "src"
                                        + separator
                                        + "test"
                                        + separator
                                        + "solidity"
                                        + separator
                                        + "org"
                                        + separator
                                        + "com"
                                        + separator
                                        + "generated"
                                        + separator
                                        + "contracts"
                                        + separator
                                        + "TestContract2Test.java")
                        .exists());
    }

    @Test
    public void testThatMessageIsThrownWhenProjectNameIsInvalid()
            throws IOException, InterruptedException {
        String[] generateArgs = {"generate", temp + separator + "badFile"};
        Process generateProcess =
                new ClassExecutor()
                        .executeClassAsSubProcessAndReturnProcess(
                                Generator.class,
                                Collections.emptyList(),
                                Arrays.asList(generateArgs))
                        .start();
        generateProcess.waitFor();
        StringWriter writer = new StringWriter();
        IOUtils.copy(generateProcess.getErrorStream(), writer, Charsets.UTF_8);
        assertEquals(
                "Looks like there is a problem with the classpath. Please use Web3j CLI to generate your project.\n",
                writer.toString());
    }

    @Test
    public void testWhenGenerateArgumentIsEmpty() throws IOException, InterruptedException {
        String[] generateArgs = {"generate"};
        Process generateProcess =
                new ClassExecutor()
                        .executeClassAsSubProcessAndReturnProcess(
                                Generator.class,
                                Collections.emptyList(),
                                Arrays.asList(generateArgs))
                        .start();
        generateProcess.waitFor();
        StringWriter writer = new StringWriter();
        IOUtils.copy(generateProcess.getErrorStream(), writer, Charsets.UTF_8);
        assertEquals("generate <project_directory>\n", writer.toString());
    }
}
