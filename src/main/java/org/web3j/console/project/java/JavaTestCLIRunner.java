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
package org.web3j.console.project.java;

import java.io.IOException;

import picocli.CommandLine;

import org.web3j.codegen.Console;

@CommandLine.Command(
        name = "generate-tests",
        mixinStandardHelpOptions = true,
        version = "4.0",
        sortOptions = false)
public class JavaTestCLIRunner implements Runnable {
    @CommandLine.Option(
            names = {"-i", "--java-wrapper-directory"},
            description = "The class path of your generated wrapper.",
            required = true)
    public String javaWrapperDir;

    @CommandLine.Option(
            names = {"-o", "--output-directory"},
            description = "The path where the unit tests will be generated.",
            required = true)
    public String unitTestOutputDir;

    @Override
    public void run() {
        try {
            new JavaTestCreator(javaWrapperDir, unitTestOutputDir).generate();
            Console.exitSuccess(
                    "Unit tests were generated successfully at location: " + unitTestOutputDir);
        } catch (IOException e) {
            Console.exitError(e);
        }
    }
}
