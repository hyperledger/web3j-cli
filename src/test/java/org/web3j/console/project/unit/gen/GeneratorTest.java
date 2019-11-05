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
import java.nio.file.NoSuchFileException;

import org.junit.jupiter.api.Test;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest extends Setup {

    @Test
    public void testThatUnitClassWasGenerated() {
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
    public void testThatExceptionIsThrownWhenFileIsNotFound() {
        String[] generateArgs = {"generate", temp + separator + "badFile"};
        assertThrows(NoSuchFileException.class, () -> Generator.main(generateArgs));
    }

    @Test
    public void testThatExceptionIsThrownWhenDirectoryIsNotGiven() {
        String[] generateArgs = {"generate"};
        assertThrows(Exception.class, () -> Generator.main(generateArgs));
    }
}
