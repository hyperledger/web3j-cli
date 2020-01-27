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
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.web3j.commons.JavaVersion;
import org.web3j.console.project.AbstractProject;
import org.web3j.console.project.Project;
import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.ProjectWallet;
import org.web3j.console.project.ProjectWriter;
import org.web3j.console.project.templates.kotlin.KotlinTemplateBuilder;
import org.web3j.console.project.templates.kotlin.KotlinTemplateProvider;
import org.web3j.console.project.utils.ProjectUtils;
import org.web3j.crypto.CipherException;

public class KotlinProject extends AbstractProject<KotlinProject> implements Project {

    protected KotlinProject(
            boolean withTests,
            boolean withFatJar,
            boolean withWallet,
            boolean withSampleCode,
            String command,
            String solidityImportPath,
            ProjectStructure projectStructure) {
        super(
                withTests,
                withFatJar,
                withWallet,
                withSampleCode,
                command,
                solidityImportPath,
                projectStructure);
    }

    protected void generateTests(ProjectStructure projectStructure) throws IOException {

        new KotlinTestCreator(
                        projectStructure.getGeneratedJavaWrappers(),
                        projectStructure.getPathToTestDirectory())
                .generate();
    }

    @Override
    protected KotlinProject getProjectInstance() {
        return this;
    }

    protected void generateWallet()
            throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
                    NoSuchProviderException, IOException {
        projectStructure.createWalletDirectory();
        projectWallet =
                new ProjectWallet(
                        ProjectUtils.generateWalletPassword(), projectStructure.getWalletPath());
        ProjectWriter.writeResourceFile(
                projectWallet.getWalletPassword(),
                projectWallet.getPasswordFileName(),
                projectStructure.getWalletPath());
    }

    public KotlinTemplateProvider getTemplateProvider() {
        KotlinTemplateBuilder templateBuilder =
                new KotlinTemplateBuilder()
                        .withProjectNameReplacement(projectStructure.projectName)
                        .withPackageNameReplacement(projectStructure.packageName)
                        .withGradleBatScript("gradlew.bat.template")
                        .withGradleScript("gradlew.template")
                        .withGradleSettings("settings.gradle.template")
                        .withWrapperGradleSettings("gradlew-wrapper.properties.template")
                        .withGradlewWrapperJar("gradle-wrapper.jar");

        if (projectWallet != null) {
            templateBuilder.withWalletNameReplacement(projectWallet.getWalletName());
            templateBuilder.withPasswordFileName(projectWallet.getPasswordFileName());
        }
        if (command.equals("new")) {
            templateBuilder
                    .withGradleBuild(
                            JavaVersion.getJavaVersionAsDouble() < 11
                                    ? "build.gradle.template"
                                    : "build.gradleJava11.template")
                    .withSolidityProject("HelloWorld.sol");

        } else if (command.equals("import")) {
            templateBuilder
                    .withGradleBuild(
                            JavaVersion.getJavaVersionAsDouble() < 11
                                    ? "build.gradleImport.template"
                                    : "build.gradleImportJava11.template")
                    .withPathToSolidityFolder(solidityImportPath);
        }

        if (withSampleCode) {
            templateBuilder.withMainKotlinClass("Kotlin.template");
        } else {
            templateBuilder.withMainKotlinClass("EmptyKotlin.template");
        }

        return templateBuilder.build();
    }

    @Override
    public void generateTests() {
        new KotlinTestCreator(projectStructure.getWrapperPath(), projectStructure.getTestPath());
    }
}
