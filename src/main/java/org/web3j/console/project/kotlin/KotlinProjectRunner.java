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
package org.web3j.console.project.kotlin;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import org.web3j.console.project.Project;
import org.web3j.console.project.ProjectCreatorConfig;
import org.web3j.console.project.ProjectRunner;

import static org.web3j.codegen.Console.exitError;

public abstract class KotlinProjectRunner extends ProjectRunner {

    public KotlinProjectRunner(final ProjectCreatorConfig projectCreatorConfig) {
        super(projectCreatorConfig);
    }

    public void generateKotlin(
            boolean withTests,
            Optional<File> solidityFile,
            boolean withFatJar,
            boolean withSampleCode,
            String command) {
        try {
            KotlinBuilder kotlinBuilder =
                    new KotlinBuilder()
                            .withProjectName(projectName)
                            .withRootDirectory(outputDir)
                            .withPackageName(packageName)
                            .withTests(withTests)
                            .withCommand(command)
                            .withSampleCode(withSampleCode)
                            .withFatJar(withFatJar);
            solidityFile.map(File::getAbsolutePath).ifPresent(kotlinBuilder::withSolidityFile);
            Project kotlinProject = kotlinBuilder.build();
            kotlinProject.createProject();
            onSuccess(kotlinProject);
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exitError("\nCould not generate project reason: \n" + sw.toString());
        }
    }
}
