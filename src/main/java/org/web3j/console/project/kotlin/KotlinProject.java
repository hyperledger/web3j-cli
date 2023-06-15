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

import java.io.IOException;

import org.web3j.console.project.AbstractProject;
import org.web3j.console.project.Project;
import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.templates.kotlin.KotlinTemplateBuilder;
import org.web3j.console.project.templates.kotlin.KotlinTemplateProvider;

public class KotlinProject extends AbstractProject<KotlinProject> implements Project {

    protected KotlinProject(
            boolean withTests,
            boolean withFatJar,
            boolean withSampleCode,
            String command,
            String solidityImportPath,
            ProjectStructure projectStructure) {
        super(withTests, withFatJar, withSampleCode, command, solidityImportPath, projectStructure);
    }

    protected void generateTests(ProjectStructure projectStructure) throws IOException {

        new KotlinTestCLIRunner(
                        projectStructure.getGeneratedJavaWrappers(),
                        projectStructure.getPathToTestDirectory())
                .generateKotlin();
    }

    @Override
    protected KotlinProject getProjectInstance() {
        return this;
    }

    public KotlinTemplateProvider getTemplateProvider() {
        KotlinTemplateBuilder templateBuilder =
                new KotlinTemplateBuilder()
                        .withProjectNameReplacement(projectStructure.projectName)
                        .withPackageNameReplacement(projectStructure.packageName)
                        .withGradleBatScript("project/gradlew.bat.template")
                        .withGradleScript("project/gradlew.template")
                        .withGradleSettings("project/settings.gradle.template")
                        .withWrapperGradleSettings("project/gradlew-wrapper.properties.template")
                        .withGradlewWrapperJar("gradle-wrapper.jar");

        if (command.equals("new")) {
            templateBuilder
                    .withGradleBuild("project/build.gradle.template")
                    .withSolidityProject("contracts/HelloWorld.sol");

        } else if (command.equals("import")) {
            templateBuilder
                    .withGradleBuild("project/build.gradleImport.template")
                    .withPathToSolidityFolder(solidityImportPath);
        }

        if (withSampleCode) {
            templateBuilder.withMainKotlinClass("project/Kotlin.template");
        } else {
            templateBuilder.withMainKotlinClass("project/EmptyKotlin.template");
        }

        return templateBuilder.build();
    }
}
