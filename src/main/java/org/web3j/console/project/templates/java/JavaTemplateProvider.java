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
package org.web3j.console.project.templates.java;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.web3j.console.project.ProjectStructure;
import org.web3j.console.project.ProjectWriter;
import org.web3j.console.project.templates.TemplateProvider;
import org.web3j.console.project.templates.TemplateReader;
import org.web3j.console.project.utils.ProjectUtils;

public class JavaTemplateProvider implements TemplateProvider {
    private final String mainJavaClass;
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
    private final String readme;

    protected JavaTemplateProvider(
            final String mainJavaClass,
            final String solidityContract,
            final String pathToSolidityFolder,
            final String gradleBuild,
            final String gradleSettings,
            final String gradlewWrapperSettings,
            final String gradlewBatScript,
            final String gradlewScript,
            final String gradlewJar,
            String packageNameReplacement,
            String projectNameReplacement,
            String readme) {
        this.mainJavaClass = mainJavaClass;
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
        this.readme = readme;
    }

    public String getSolidityContract() {
        return solidityContract;
    }

    public String getMainJavaClass() {
        return mainJavaClass;
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

    public String loadMainJavaClass() throws IOException {
        return TemplateReader.readFile(mainJavaClass)
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
                loadMainJavaClass(),
                ProjectUtils.capitalizeFirstLetter(projectStructure.getProjectName() + ".java"),
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
        if (readme != null)
            ProjectWriter.copyResourceFile(
                    readme, projectStructure.getProjectRoot() + File.separator + "README.md");
    }
}
