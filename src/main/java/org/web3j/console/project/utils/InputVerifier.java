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

import java.io.PrintStream;
import javax.lang.model.SourceVersion;

public class InputVerifier {

    private final PrintStream outputStream;

    public InputVerifier() {
        this(System.out);
    }

    public InputVerifier(final PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public boolean requiredArgsAreNotEmpty(final String... args) {
        for (final String argument : args) {
            if (argument.trim().isEmpty()) {
                outputStream.println("Please make sure the required parameters are not empty.");
                return false;
            }
        }
        return true;
    }

    public boolean classNameIsValid(final String className) {
        if (!SourceVersion.isIdentifier(className) || SourceVersion.isKeyword(className)) {
            outputStream.println(
                    className
                            + " is not valid name. Please make sure that your project name complies with Java's class naming convention.");
            return false;
        }
        return true;
    }

    public boolean packageNameIsValid(final String packageName) {
        String[] splitPackageName = packageName.split("[.]");
        for (final String argument : splitPackageName) {
            if (!SourceVersion.isIdentifier(argument) || SourceVersion.isKeyword(argument)) {
                outputStream.println(
                        argument
                                + " is not a valid package name. Please make sure that your project package name complies with Java's package naming convention.");
                return false;
            }
        }
        return true;
    }
}
