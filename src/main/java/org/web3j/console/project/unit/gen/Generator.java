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
import java.net.URL;
import java.net.URLClassLoader;

public class Generator {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String pathToProject =
                "/home/alexander/Documents/dev/temp/Test/build/generated/source/web3j/main/java/org/com/generated/contracts/Greeter.java";
        CompilerClassLoader compilerClassLoader =
                new CompilerClassLoader(
                        new File("/home/alexander/Documents/dev/otherClasses"),
                        new File(
                                        "/home/alexander/Documents/dev/generated/Test/build/generated/source/web3j/main/java/org/com/generated/contracts")
                                .toURI()
                                .toURL());
        ClassLoader cl =
                new URLClassLoader(
                        new URL[] {new File("/home/alexander/Documents/dev/otherClasses").toURL()});
        Class test = cl.loadClass("org.com.generated.contracts.Greeter");
        File[] generatedContracts = new File(pathToProject).listFiles();
        assert generatedContracts != null;

        new ContractTestClassGenerator(test)
                .writeClass(new File("/home/alexander/Documents/dev/otherClasses/generated"));
    }

    private static Class getClassFromFile(File javaFile) throws Exception {
        return null;
    }
}
