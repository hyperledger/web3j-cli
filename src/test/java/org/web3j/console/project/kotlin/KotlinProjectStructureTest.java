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
package org.web3j.console.project.kotlin;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.java.JavaProjectStructure;
import org.web3j.console.project.utils.Folders;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KotlinProjectStructureTest {
    private ProjectStructure projectStructure;
    private String tempDirPath;

    @BeforeEach
    public void init() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
        projectStructure = new KotlinProjectStructure(tempDirPath, "test.test", "Test");
        projectStructure.createMainDirectory();
        projectStructure.createTestDirectory();
        projectStructure.createSolidityDirectory();
        projectStructure.createWrapperDirectory();
        projectStructure.createTestDirectory();
    }

    @Test
    public void getRootTest() {
        assertEquals(projectStructure.getRootDirectory(), tempDirPath);
    }

    @Test
    public void getProjectRootTest() {
        assertEquals(projectStructure.getProjectRoot(), tempDirPath + File.separator + "Test");
    }

    @Test
    public void getPackageNameTest() {
        assertEquals("test.test", projectStructure.getPackageName());
    }

    @Test
    public void getProjectName() {
        assertEquals("Test", projectStructure.getProjectName());
    }

    @Test
    public void getTestPathTest() {
        final String testPath =
                tempDirPath
                        + File.separator
                        + "Test"
                        + File.separator
                        + "src"
                        + File.separator
                        + "test"
                        + File.separator
                        + "kotlin"
                        + File.separator
                        + "test"
                        + File.separator
                        + "test"
                        + File.separator;

        assertEquals(testPath, projectStructure.getTestPath());
    }

    @Test
    public void getSolidityPathTest() {
        final String solidityPath =
                tempDirPath
                        + File.separator
                        + "Test"
                        + File.separator
                        + "src"
                        + File.separator
                        + "main"
                        + File.separator
                        + "solidity"
                        + File.separator;
        assertEquals(solidityPath, projectStructure.getSolidityPath());
    }

    @Test
    public void getMainPathTest() {
        final String mainPath =
                tempDirPath
                        + File.separator
                        + "Test"
                        + File.separator
                        + "src"
                        + File.separator
                        + "main"
                        + File.separator
                        + "kotlin"
                        + File.separator
                        + "test"
                        + File.separator
                        + "test"
                        + File.separator;

        assertEquals(mainPath, projectStructure.getMainPath());
    }

    @Test
    public void getWrapperPathTest() {
        final String wrapperPath =
                tempDirPath
                        + File.separator
                        + "Test"
                        + File.separator
                        + "gradle"
                        + File.separator
                        + "wrapper"
                        + File.separator;

        assertEquals(wrapperPath, projectStructure.getWrapperPath());
    }

    @Test
    public void getRootUserDir() {
        ProjectStructure projectStructure = new JavaProjectStructure("~", "test.test", "Test");
        assertEquals(projectStructure.getRootDirectory(), System.getProperty("user.home"));
    }

    @Test
    public void getRootUserDirSubfolder() {
        ProjectStructure projectStructure =
                new JavaProjectStructure(
                        "~" + File.separator + "a" + File.separator + "b" + File.separator + "c",
                        "test.test",
                        "Test");
        assertEquals(
                projectStructure.getRootDirectory(),
                System.getProperty("user.home")
                        + File.separator
                        + "a"
                        + File.separator
                        + "b"
                        + File.separator
                        + "c");
    }

    @Test
    public void getRootSpecialTildeCase() {
        ProjectStructure projectStructure =
                new JavaProjectStructure(
                        File.separator
                                + "root"
                                + File.separator
                                + "~"
                                + File.separator
                                + "a"
                                + File.separator
                                + "b"
                                + File.separator
                                + "c",
                        "test.test",
                        "Test");
        assertEquals(
                projectStructure.getRootDirectory(),
                File.separator
                        + "root"
                        + File.separator
                        + "~"
                        + File.separator
                        + "a"
                        + File.separator
                        + "b"
                        + File.separator
                        + "c");
    }
}
