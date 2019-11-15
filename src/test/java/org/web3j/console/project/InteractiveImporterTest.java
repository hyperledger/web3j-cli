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
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InteractiveImporterTest {
    private static final String FORMATTED_SOLIDITY_PATH =
            "/web3j/console/src/test/resources/Solidity".replace("/", File.separator);
    private InputStream inputStream;
    private String tempDirPath;

    @BeforeEach
    void setup(@TempDir Path temp) {
        tempDirPath = temp.toString();

        final String input =
                "Test\norg.com\n" + FORMATTED_SOLIDITY_PATH + "\n" + tempDirPath + "\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
    }

    @Test
    public void runInteractiveModeTest() {
        final InteractiveImporter options = new InteractiveImporter(inputStream, System.out);
        assertEquals("Test", options.getProjectName());
        assertEquals("org.com", options.getPackageName());
        assertEquals(FORMATTED_SOLIDITY_PATH, options.getSolidityProjectPath());
        assertEquals(Optional.of(tempDirPath), options.getProjectDestination());
    }
}
