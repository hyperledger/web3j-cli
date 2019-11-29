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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectWriterTest {

    private static final ProjectWriter projectWriter = new ProjectWriter();
    private static final TemplateProvider templateProvider =
            new TemplateProvider.Builder().loadGradleJar("gradle-wrapper.jar").build();

    private String tempDirPath;

    @BeforeEach
    void setup(@TempDir Path temp) {
        tempDirPath = temp.toString();
    }

    @Test
    public void writeResourceFileTest() throws Exception {
        projectWriter.writeResourceFile("HelloWorld.sol", "HelloWorld.sol", tempDirPath);
        assertTrue(new File(tempDirPath + File.separator + "HelloWorld.sol").exists());
    }

    @Test
    public void copyResourceFileTest() throws IOException {
        projectWriter.copyResourceFile(
                templateProvider.getGradlewJar(),
                tempDirPath + File.separator + "gradle-wrapper.jar");
        assertTrue(new File(tempDirPath + File.separator + "gradle-wrapper.jar").exists());
    }

    @Test
    public void importSolidityProjectTest() throws IOException {
        final File file = new File(tempDirPath + File.separator + "tempSolidityDir");
        file.mkdirs();
        projectWriter.writeResourceFile(
                "HelloWorld.sol",
                "HelloWorld.sol",
                tempDirPath + File.separator + "tempSolidityDir");
        projectWriter.importSolidityProject(
                new File(tempDirPath + File.separator + "tempSolidityDir"),
                tempDirPath + File.separator + "tempSolidityDestination");
        assertTrue(
                new File(
                                tempDirPath
                                        + File.separator
                                        + "tempSolidityDestination"
                                        + File.separator
                                        + "HelloWorld.sol")
                        .exists());
    }
}
