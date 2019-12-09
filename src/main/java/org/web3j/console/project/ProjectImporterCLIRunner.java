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
import java.io.PrintWriter;
import java.io.StringWriter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.web3j.console.project.utils.InputVerifier;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.project.InteractiveOptions.overrideExistingProject;
import static org.web3j.console.project.ProjectImporter.COMMAND_IMPORT;
import static org.web3j.console.project.utils.ProjectUtils.*;
import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(name = COMMAND_IMPORT)
public class ProjectImporterCLIRunner extends ProjectCreatorCLIRunner {

    @Option(
            names = {"-s", "--solidity-path"},
            description = "Path to solidity file/folder",
            required = true)
    String solidityImportPath;

    @Option(
            names = {"-t", "--generate-tests"},
            description = "Generate unit tests for the contract wrappers",
            required = false,
            showDefaultValue = ALWAYS)
    boolean generateTests = false;

    @Override
    public void run() {
        if (inputIsValid(projectName, packageName, solidityImportPath)) {
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
            ProjectImporter projectImporter =
                    new ProjectImporter(outputDir, packageName, projectName, solidityImportPath);
            projectImporter.generate(generateTests);
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("Could not generate project reason:" + sw.toString());
        }
    }
}
