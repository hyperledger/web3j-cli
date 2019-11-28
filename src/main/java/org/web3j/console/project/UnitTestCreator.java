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

import org.web3j.codegen.Console;
import org.web3j.codegen.unit.gen.ClassProvider;
import org.web3j.codegen.unit.gen.UnitClassGenerator;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.web3j.utils.Collection.tail;

public class UnitTestCreator {
    public static final String COMMAND_GENERATE_TESTS = "generate-tests";

    private final String writePath;
    private final String wrapperPath;

    public UnitTestCreator(final String wrapperPath, final String writePath) {
        this.writePath = writePath;
        this.wrapperPath = wrapperPath;
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals(COMMAND_GENERATE_TESTS)) {
            args = tail(args);
            if (args.length == 0) {
                final List<String> listOfArgs = new ArrayList<>();
                InteractiveOptions.getGeneratedWrapperLocation()
                        .ifPresent(
                                wrappersPath -> {
                                    listOfArgs.add("-i");
                                    listOfArgs.add(wrappersPath);
                                });
                InteractiveOptions.setGeneratedTestLocation()
                        .ifPresent(
                                outputPath -> {
                                    listOfArgs.add("-o");
                                    listOfArgs.add(outputPath);
                                });
                args = listOfArgs.toArray(new String[0]);
            }
        }
        CommandLine.run(new UnitTestCLIRunner(), args);
    }

    void generate() throws IOException {
        List<Class> compiledClasses = new ClassProvider(new File(wrapperPath)).getClasses();
        compiledClasses.forEach(
                compiledClass -> {
                    try {
                        new UnitClassGenerator(
                                        compiledClass,
                                        compiledClass
                                                .getCanonicalName()
                                                .substring(
                                                        0,
                                                        compiledClass
                                                                .getCanonicalName()
                                                                .lastIndexOf(".")),
                                        writePath)
                                .writeClass();
                    } catch (IOException e) {
                        Console.exitError(e);
                    }
                });
    }
}
