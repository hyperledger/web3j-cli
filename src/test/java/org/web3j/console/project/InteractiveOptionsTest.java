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
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InteractiveOptionsTest {
    private String formattedPath =
            new File(String.join(separator, "src", "test", "resources", "Solidity"))
                    .getAbsolutePath();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    void setup(@TempDir Path temp) {
        tempDirPath = temp.toString();
        final String input = "Test\norg.com\n" + formattedPath + "\n" + tempDirPath + "\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void runInteractiveModeTest() {
        assertEquals("Test", InteractiveOptions.getProjectName());
        assertEquals("org.com", InteractiveOptions.getPackageName());
        assertEquals(formattedPath, InteractiveOptions.getSolidityProjectPath());
        assertEquals(tempDirPath, InteractiveOptions.getProjectDestination("Test").get());
    }
}
