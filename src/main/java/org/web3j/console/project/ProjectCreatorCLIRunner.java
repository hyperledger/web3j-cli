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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.web3j.crypto.CipherException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.web3j.console.project.utils.InputVerifier;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.project.InteractiveOptions.overrideExistingProject;
import static org.web3j.console.project.ProjectCreator.COMMAND_NEW;
import static org.web3j.console.project.utils.ProjectUtils.deleteFolder;
import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(name = COMMAND_NEW, mixinStandardHelpOptions = true, version = "4.0", sortOptions = false)
public class ProjectCreatorCLIRunner implements Runnable {
    @Option(
            names = {"-o", "--output-dir"},
            description = "Destination base directory.",
            required = false,
            showDefaultValue = ALWAYS)
    String outputDir = System.getProperty("user.dir");

    @Option(
            names = {"-p", "--package"},
            description = "Base package name.",
            required = true)
    String packageName;

    @Option(
            names = {"-n", "--project-name"},
            description = "Project name.",
            required = true)
    String projectName;

    @Override
    public void run() {
        if (inputIsValid(projectName, packageName)) {
            if (InputVerifier.projectExists(new File(projectName))) {
                if (overrideExistingProject()) {
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

    private void createProject() {
        try {
            new ProjectCreator(outputDir, packageName, projectName).generate();
        } catch (final IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CipherException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("Could not generate project reason:" + sw.toString());
        }
    }

    boolean inputIsValid(String... requiredArgs) {
        return InputVerifier.requiredArgsAreNotEmpty(requiredArgs)
                && InputVerifier.classNameIsValid(projectName)
                && InputVerifier.packageNameIsValid(packageName);
    }
}
