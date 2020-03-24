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

import picocli.CommandLine;

import org.web3j.console.project.utils.InputVerifier;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.project.utils.ProjectUtils.deleteFolder;
import static picocli.CommandLine.Help.Visibility.ALWAYS;

public abstract class ProjectCreatorCLIRunner implements Runnable {
    @CommandLine.Option(
            names = {"-o", "--output-dir"},
            description = "Destination base directory.",
            required = false,
            showDefaultValue = ALWAYS)
    public String outputDir = System.getProperty("user.dir");

    @CommandLine.Option(
            names = {"-p", "--package"},
            description = "Base package name.",
            required = true)
    public String packageName;

    @CommandLine.Option(
            names = {"-n", "--project-name"},
            description = "Project name.",
            required = true)
    public String projectName;

    @Override
    public void run() {
        if (inputIsValid(projectName, packageName)) {
            if (InputVerifier.projectExists(new File(projectName))) {
                if (new InteractiveOptions().overrideExistingProject()) {
                    deleteFolder(new File(projectName).toPath());
                    createProject();
                } else {
                    exitError("Project creation was canceled.");
                }
            } else {
                createProject();
            }
        }
    }

    protected abstract void createProject();

    boolean inputIsValid(String... requiredArgs) {
        return InputVerifier.requiredArgsAreNotEmpty(requiredArgs)
                && InputVerifier.classNameIsValid(projectName)
                && InputVerifier.packageNameIsValid(packageName);
    }
}
