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
package org.web3j.console.project.templates.kotlin;

import java.io.File;
import java.io.IOException;

import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.ProjectWriter;
import org.web3j.console.project.templates.TemplateProvider;
import org.web3j.console.project.utils.InputVerifier;

public class KotlinTemplateProvider extends TemplateProvider {

    KotlinTemplateProvider(
            String mainJavaClass,
            String solidityContract,
            String pathToSolidityFolder,
            String gradleBuild,
            String gradleSettings,
            String gradlewWrapperSettings,
            String gradlewBatScript,
            String gradlewScript,
            String gradlewJar,
            String packageNameReplacement,
            String projectNameReplacement,
            String passwordFileName,
            String walletNameReplacement) {
        super(
                mainJavaClass,
                solidityContract,
                pathToSolidityFolder,
                gradleBuild,
                gradleSettings,
                gradlewWrapperSettings,
                gradlewBatScript,
                gradlewScript,
                gradlewJar,
                packageNameReplacement,
                projectNameReplacement,
                passwordFileName,
                walletNameReplacement);
    }

    public void generateFiles(ProjectStructure projectStructure) throws IOException {
        ProjectWriter.writeResourceFile(
                loadMainJavaClass(),
                InputVerifier.capitalizeFirstLetter(projectStructure.getProjectName() + ".kt"),
                projectStructure.getMainPath());
        ProjectWriter.writeResourceFile(
                loadGradleBuild(), "build.gradle", projectStructure.getProjectRoot());
        ProjectWriter.writeResourceFile(
                loadGradleSettings(), "settings.gradle", projectStructure.getProjectRoot());
        if (solidityContract != null)
            ProjectWriter.writeResourceFile(
                    loadSolidityContract(), "HelloWorld.sol", projectStructure.getSolidityPath());
        if (pathToSolidityFolder != null) {
            ProjectWriter.importSolidityProject(
                    new File(pathToSolidityFolder), projectStructure.getSolidityPath());
        }
        ProjectWriter.writeResourceFile(
                loadGradlewWrapperSettings(),
                "gradle-wrapper.properties",
                projectStructure.getWrapperPath());
        ProjectWriter.writeResourceFile(
                loadGradlewScript(), "gradlew", projectStructure.getProjectRoot());
        ProjectWriter.writeResourceFile(
                loadGradlewBatScript(), "gradlew.bat", projectStructure.getProjectRoot());
        ProjectWriter.copyResourceFile(
                getGradlewJar(),
                projectStructure.getWrapperPath() + File.separator + "gradle-wrapper.jar");
    }
}
