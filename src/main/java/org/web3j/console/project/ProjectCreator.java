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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import picocli.CommandLine;

import org.web3j.console.project.utills.InputVerifier;

import static java.io.File.separator;
import static org.web3j.codegen.Console.exitError;
import static org.web3j.codegen.Console.exitSuccess;
import static org.web3j.utils.Collection.tail;

public class ProjectCreator {

    public static final String COMMAND_NEW = "new";

    private final ProjectStructure projectStructure;
    private final TemplateProvider templateProvider;
    private final String projectName;

    ProjectCreator(final String root, final String packageName, final String projectName)
            throws IOException {
        this.projectName = projectName;
        this.projectStructure = new ProjectStructure(root, packageName, projectName);
        this.templateProvider =
                new TemplateProvider.Builder()
                        .loadGradlewBatScript("gradlew.bat.template")
                        .loadGradlewScript("gradlew.template")
                        .loadMainJavaClass("Template.java")
                        .loadGradleBuild("build.gradle.template")
                        .loadGradleSettings("settings.gradle.template")
                        .loadGradlewWrapperSettings("gradlew-wrapper.properties.template")
                        .loadGradleJar("gradle-wrapper.jar")
                        .loadSolidityGreeter("HelloWorld.sol")
                        .withPackageNameReplacement(s -> s.replace("<package_name>", packageName))
                        .withProjectNameReplacement(
                                s ->
                                        s.replace(
                                                "<project_name>",
                                                InputVerifier.capitalizeFirstLetter(projectName)))
                        .build();
    }

    public static void main(String[] args) {
        final String projectName;
        final List<String> stringOptions = new ArrayList<>();
        if (args.length > 0 && args[0].equals(COMMAND_NEW)) {
            args = tail(args);
            if (args.length == 0) {
                stringOptions.add("-n");
                projectName = InteractiveOptions.getProjectName();
                stringOptions.add(projectName);
                stringOptions.add("-p");
                stringOptions.add(InteractiveOptions.getPackageName());
                InteractiveOptions.getProjectDestination(projectName)
                        .ifPresent(
                                projectDest -> {
                                    stringOptions.add("-o");
                                    stringOptions.add(projectDest);
                                });
                args = stringOptions.toArray(new String[0]);
            }
        }

        CommandLine.run(new ProjectCreatorCLIRunner(), args);
    }

    void generate() {
        generate(true, Optional.empty());
    }

    void generate(boolean withTests, Optional<File> solidityFile) {
        try {
            Project.Builder builder =
                    Project.builder()
                            .withProjectStructure(projectStructure)
                            .withTemplateProvider(templateProvider);
            solidityFile.ifPresent(builder::withSolidityFile);
            builder.build();
            if (withTests) {
                generateTests();
            }
            onSuccess();
        } catch (final Exception e) {
            exitError(e);
        }
    }

    private void generateTests() throws IOException {
        String wrapperPath =
                String.join(
                        separator,
                        projectStructure.getRoot(),
                        projectName,
                        "build",
                        "generated",
                        "source",
                        "web3j",
                        "main",
                        "java");
        String writePath =
                String.join(
                        separator, projectStructure.getRoot(), projectName, "src", "test", "java");
        new UnitTestCreator(wrapperPath, writePath).generate();
    }

    private void onSuccess() {
        exitSuccess(
                "Project created with name: "
                        + projectStructure.getProjectName()
                        + " at location: "
                        + projectStructure.getProjectRoot());
    }
}
