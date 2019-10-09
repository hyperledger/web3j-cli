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

import static java.io.File.*;
import static org.web3j.utils.Collection.tail;

public class Generator {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        args = tail(args);
        File pathToJavaContracts =
                new File(
                        args[0]
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
        ClassProvider classProvider = new ClassProvider(pathToJavaContracts);

        String[] finalArgs = args;
        classProvider
                .getClasses()
                .forEach(
                        c -> {
                            try {
                                new TestClassProvider(
                                                c,
                                                c.getCanonicalName()
                                                        .substring(
                                                                0,
                                                                c.getCanonicalName()
                                                                        .lastIndexOf(".")),
                                                finalArgs[0])
                                        .writeClass();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
    }
}
