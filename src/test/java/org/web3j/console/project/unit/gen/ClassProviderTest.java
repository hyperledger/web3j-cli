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
import java.util.List;

import org.junit.jupiter.api.Test;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassProviderTest extends Setup {

    @Test
    public void testThatAllClassesWereSuccessfullyLoaded()
            throws IOException, ClassNotFoundException {
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
    public void testThatExceptionIsThrownWhenClassIsNotFound() {
        File pathToProject =
                new File(
                        temp.getPath()
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

        assertThrows(
                ClassNotFoundException.class, () -> new ClassProvider(pathToProject).getClasses());
    }

    @Test
    public void testThatTheClassPathNameWasSuccessfullyLoaded()
            throws IOException, ClassNotFoundException {
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
        List<Class> listOfClasses = classProvider.getClasses();
        assertTrue(listOfClasses.get(0).getCanonicalName().contains("org.com"));
    }
}
