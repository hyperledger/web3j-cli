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
package org.web3j.console.project.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputVerifierTest {

    InputVerifier inputVerifier = new InputVerifier();

    @Test
    public void requiredArgumentsAreEmptyTest() {
        final String[] args = {"", "", ""};
        assertFalse(inputVerifier.requiredArgsAreNotEmpty(args));
    }

    @Test
    public void requiredArgsAreNotEmptyTest() {
        final String[] args = {
            "TestProjectName", "test.package.name",
        };
        assertTrue(inputVerifier.requiredArgsAreNotEmpty(args));
    }

    @Test
    public void classNameIsValidTest() {
        assertTrue(inputVerifier.classNameIsValid("ClassNameTest"));
    }

    @Test
    public void classNameIsNotValidWhenFirstCharacterIsNumberTest() {
        assertFalse(inputVerifier.classNameIsValid("1BadClassName"));
    }

    @Test
    public void ClassNameIsNotValidWhenFirstCharacterIsSymbol() {
        assertFalse(inputVerifier.classNameIsValid("!BadClassName"));
    }

    @Test
    public void packageNameIsValidTest() {
        assertTrue(inputVerifier.packageNameIsValid("org.com"));
    }

    @Test
    public void packageNameIsNotValidTest() {
        assertFalse(inputVerifier.packageNameIsValid("1.com"));
    }
}
