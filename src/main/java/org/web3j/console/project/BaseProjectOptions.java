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

public class BaseProjectOptions {
    @Option(
            names = {"-n", "--project-name"},
            description = "Project name.",
            showDefaultValue = ALWAYS)
    public String projectName = "Web3App";

    @Option(
            names = {"-p", "--package"},
            description = "Base package name.",
            showDefaultValue = ALWAYS)
    public String packageName = "org.web3j";

    @Option(
            names = {"-o", "--output-dir"},
            description = "Destination base directory.",
            showDefaultValue = ALWAYS)
    public String outputDir = ".";

    @Option(
            names = {"--overwrite"},
            description = {"overwrite the project if exists."},
            showDefaultValue = ALWAYS)
    public Boolean overwrite = false;
}
