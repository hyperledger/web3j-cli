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

import picocli.CommandLine.Option;

import static picocli.CommandLine.Help.Visibility.ALWAYS;

public class ProjectOptions extends BaseProjectOptions {
    @Option(
            names = {"-t", "--generate-tests"},
            description = "Generate unit tests for the contract wrappers.",
            showDefaultValue = ALWAYS)
    public Boolean generateTests = true;

    @Option(
            names = {"--jar"},
            description = {"Generate the JAR"},
            showDefaultValue = ALWAYS)
    public Boolean generateJar = false;

    @Option(
            names = {"--kotlin"},
            description = "Generate Kotlin code instead of Java.",
            showDefaultValue = ALWAYS)
    public Boolean isKotlin = false;
}
