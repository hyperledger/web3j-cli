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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.web3j.console.project.utils.Folders;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InteractiveOptionsTest {
    private String formattedPath =
            new File(String.join(separator, "src", "test", "resources", "Solidity"))
                    .getAbsolutePath();
    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    void setup() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
        final String input =
                "Test\norg.com\n"
                        + formattedPath
                        + "\n"
                        + tempDirPath
                        + "\n"
                        + "y"
                        + "\n"
                        + ""
                        + "\n"
                        + "y"
                        + "\n"
                        + "y"
                        + "\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
    }

    @Test
    public void runInteractiveModeTest() {
        InteractiveOptions interactiveOptions =
                new InteractiveOptions(inputStream, new PrintStream(new ByteArrayOutputStream()));
        assertEquals("Test", interactiveOptions.getProjectName());
        assertEquals("org.com", interactiveOptions.getPackageName());
        assertEquals(formattedPath, interactiveOptions.getSolidityProjectPath());
        assertEquals(tempDirPath, interactiveOptions.getProjectDestination("Test").get());
        assertTrue(interactiveOptions.userWantsTests());
        assertEquals(
                String.join(separator, System.getProperty("user.dir"), "src", "test", "java"),
                interactiveOptions.setGeneratedTestLocationJava().get());
        assertTrue(interactiveOptions.overrideExistingProject());
    }
}
