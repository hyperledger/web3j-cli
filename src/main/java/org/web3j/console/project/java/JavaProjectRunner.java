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
package org.web3j.console.project.java;

import java.io.File;
import java.util.Optional;

import org.web3j.console.openapi.utils.PrettyPrinter;
import org.web3j.console.openapi.utils.SimpleFileLogger;
import org.web3j.console.project.Project;
import org.web3j.console.project.ProjectCreatorConfig;
import org.web3j.console.project.ProjectRunner;

public abstract class JavaProjectRunner extends ProjectRunner {

    public JavaProjectRunner(final ProjectCreatorConfig projectCreatorConfig) {
        super(projectCreatorConfig);
    }

    public void generateJava(
            boolean withTests,
            Optional<File> solidityFile,
            boolean withFatJar,
            boolean withSampleCode,
            String command) {
        try {
            JavaBuilder javaBuilder =
                    new JavaBuilder()
                            .withProjectName(projectName)
                            .withRootDirectory(outputDir)
                            .withPackageName(packageName)
                            .withTests(withTests)
                            .withCommand(command)
                            .withSampleCode(withSampleCode)
                            .withFatJar(withFatJar);
            solidityFile.map(File::getAbsolutePath).ifPresent(javaBuilder::withSolidityFile);
            Project javaProject = javaBuilder.build();
            javaProject.createProject();
            onSuccess(javaProject);
        } catch (final Exception e) {
            e.printStackTrace(SimpleFileLogger.INSTANCE.getFilePrintStream());
            PrettyPrinter.INSTANCE.onFailed();
            System.exit(1);
        }
    }
}
