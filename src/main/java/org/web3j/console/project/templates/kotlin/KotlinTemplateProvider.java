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
import java.util.Optional;

import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.ProjectWriter;
import org.web3j.console.project.templates.TemplateProvider;
import org.web3j.console.project.templates.TemplateReader;
import org.web3j.console.project.utils.ProjectUtils;

public class KotlinTemplateProvider implements TemplateProvider {
    private final String mainKotlinClass;
    protected final String solidityContract;
    protected final String pathToSolidityFolder;
    private final String gradleBuild;
    private final String gradleSettings;
    private final String gradlewWrapperSettings;
    private final String gradlewBatScript;
    private final String gradlewScript;
    private final String gradlewJar;
    private final Optional<String> packageNameReplacement;
    private final Optional<String> projectNameReplacement;

    protected KotlinTemplateProvider(
            final String mainKotlinClass,
            final String solidityContract,
            final String pathToSolidityFolder,
            final String gradleBuild,
            final String gradleSettings,
            final String gradlewWrapperSettings,
            final String gradlewBatScript,
            final String gradlewScript,
            final String gradlewJar,
            String packageNameReplacement,
            String projectNameReplacement) {
        this.mainKotlinClass = mainKotlinClass;
        this.solidityContract = solidityContract;
        this.pathToSolidityFolder = pathToSolidityFolder;
        this.gradleBuild = gradleBuild;
        this.gradleSettings = gradleSettings;
        this.gradlewWrapperSettings = gradlewWrapperSettings;
        this.gradlewBatScript = gradlewBatScript;
        this.gradlewScript = gradlewScript;
        this.gradlewJar = gradlewJar;
        this.packageNameReplacement = Optional.ofNullable(packageNameReplacement);
        this.projectNameReplacement = Optional.ofNullable(projectNameReplacement);
    }

    public String getSolidityContract() {
        return solidityContract;
    }

    public String getMainKotlinClass() {
        return mainKotlinClass;
    }

    public String getGradleBuild() {
        return gradleBuild;
    }

    public String getGradleSettings() {
        return gradleSettings;
    }

    public String getGradlewWrapperSettings() {
        return gradlewWrapperSettings;
    }

    public String getGradlewBatScript() {
        return gradlewBatScript;
    }

    public String getGradlewScript() {
        return gradlewScript;
    }

    public String getGradlewJar() {
        return gradlewJar;
    }

    public String loadMainKotlinClass() throws IOException {
        return TemplateReader.readFile(mainKotlinClass)
                .replaceAll(
                        "<project_name>",
                        ProjectUtils.capitalizeFirstLetter(projectNameReplacement.orElse("")))
                .replaceAll("<package_name>", packageNameReplacement.orElse(""))
                .replaceAll("<project_language>", "java");
    }

    public String loadGradleBuild() throws IOException {
        return TemplateReader.readFile(gradleBuild)
                .replaceAll("<package_name>", packageNameReplacement.orElse(""))
                .replaceAll("<project_name>", projectNameReplacement.orElse(""));
    }

    public String loadSolidityContract() throws IOException {
        return TemplateReader.readFile(solidityContract);
    }

    public String loadGradleSettings() throws IOException {
        return TemplateReader.readFile(gradleSettings)
                .replaceAll("<project_name>", projectNameReplacement.orElse(""));
    }

    public String loadGradlewWrapperSettings() throws IOException {

        return TemplateReader.readFile(gradlewWrapperSettings);
    }

    public String loadGradlewBatScript() throws IOException {

        return TemplateReader.readFile(gradlewBatScript);
    }

    public String loadGradlewScript() throws IOException {

        return TemplateReader.readFile(gradlewScript);
    }

    public void generateFiles(ProjectStructure projectStructure) throws IOException {
        ProjectWriter.writeResourceFile(
                loadMainKotlinClass(),
                ProjectUtils.capitalizeFirstLetter(projectStructure.getProjectName() + ".kt"),
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
                TemplateReader.readFile("project/Dockerfile.template"),
                "Dockerfile",
                projectStructure.getProjectRoot());
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
                new File(projectStructure.getWrapperPath(), "gradle-wrapper.jar")
                        .getAbsolutePath());
    }
}
