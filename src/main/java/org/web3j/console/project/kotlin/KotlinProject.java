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
import org.web3j.console.project.BaseProject;
import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.templates.TemplateBuilder;
import org.web3j.console.project.templates.kotlin.KotlinTemplateBuilder;
import org.web3j.console.project.templates.kotlin.KotlinTemplateProvider;
import org.web3j.crypto.CipherException;

public class KotlinProject extends BaseProject {
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

    public KotlinTemplateProvider getTemplateProvider() {
        TemplateBuilder templateBuilder =
                new KotlinTemplateBuilder()
                        .withProjectNameReplacement(projectStructure.projectName)
                        .withPackageNameReplacement(projectStructure.packageName)
                        .withGradleBatScript("gradlew.bat.template")
                        .withGradleScript("gradlew.template");
        if (projectWallet != null) {

            templateBuilder.withWalletNameReplacement(projectWallet.getWalletName());
            templateBuilder.withPasswordFileName(projectWallet.getPasswordFileName());
        }
        if (command.equals("kotlin")) {
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
        templateBuilder
                .withGradleSettings("settings.gradle.template")
                .withWrapperGradleSettings("gradlew-wrapper.properties.template")
                .withGradlewWrapperJar("gradle-wrapper.jar");
        if (withSampleCode) {
            templateBuilder.withMainJavaClass("Template-Kotlin");
        } else {
            templateBuilder.withMainJavaClass("EmptyTemplate.java");
        }

        return (KotlinTemplateProvider) templateBuilder.build();
    }

    public void createProject()
            throws IOException, InterruptedException, NoSuchAlgorithmException,
                    NoSuchProviderException, InvalidAlgorithmParameterException, CipherException {
        generateTopLevelDirectories(projectStructure);
        if (withWallet) {
            generateWallet();
        }
        getTemplateProvider().generateFiles(projectStructure);
        progressCounter.processing("Creating " + projectStructure.projectName);
        buildGradleProject(projectStructure.getProjectRoot());

        if (withTests) {
            generateTests(projectStructure);
        }
        if (withFatJar) {
            createFatJar(projectStructure.getProjectRoot());
        }
        progressCounter.setLoading(false);
    }
}
