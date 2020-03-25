/*
 * Copyright 2020 Web3 Labs Ltd.
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
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine;

import org.web3j.codegen.Console;
import org.web3j.codegen.unit.gen.ClassProvider;
import org.web3j.codegen.unit.gen.java.JavaClassGenerator;
import org.web3j.codegen.unit.gen.kotlin.KotlinClassGenerator;
import org.web3j.console.project.java.JavaTestCLIRunner;
import org.web3j.console.project.kotlin.KotlinTestCLIRunner;

import static org.web3j.console.project.ProjectCreator.COMMAND_JAVA;
import static org.web3j.utils.Collection.tail;

public class UnitTestCreator {
    public static final String COMMAND_GENERATE_TESTS = "generate-tests";
    private final String writePath;
    private final String wrapperPath;

    public UnitTestCreator(String wrapperPath, String writePath) {
        this.writePath = writePath;
        this.wrapperPath = wrapperPath;
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].toLowerCase().equals(COMMAND_JAVA)) {
            args = tail(args);
            args = getValue(args);
            CommandLine.run(new JavaTestCLIRunner(), args);
        } else {
            args = getValue(args);
            CommandLine.run(new KotlinTestCLIRunner(), args);
        }
    }

    private static String[] getValue(String[] args) {
        if (args.length == 0) {
            InteractiveOptions interactiveOptions = new InteractiveOptions();
            final List<String> listOfArgs = new ArrayList<>();
            interactiveOptions
                    .getGeneratedWrapperLocation()
                    .ifPresent(
                            wrappersPath -> {
                                listOfArgs.add("-i");
                                listOfArgs.add(wrappersPath);
                            });
            interactiveOptions
                    .setGeneratedTestLocationJava()
                    .ifPresent(
                            outputPath -> {
                                listOfArgs.add("-o");
                                listOfArgs.add(outputPath);
                            });
            args = listOfArgs.toArray(new String[0]);
        }
        return args;
    }

    public void generateKotlin() throws IOException {

        List<Class> compiledClasses = new ClassProvider(new File(wrapperPath)).getClasses();
        compiledClasses.forEach(
                compiledClass -> {
                    try {
                        new KotlinClassGenerator(
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void generateJava() throws IOException {
        List<Class> compiledClasses = new ClassProvider(new File(wrapperPath)).getClasses();
        compiledClasses.forEach(
                compiledClass -> {
                    try {
                        new JavaClassGenerator(
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
