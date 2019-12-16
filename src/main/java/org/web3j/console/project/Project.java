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

import org.web3j.codegen.Console;
import org.web3j.commons.JavaVersion;
import org.web3j.console.project.utils.InputVerifier;
import org.web3j.console.project.utils.ProgressCounter;
import org.web3j.console.project.utils.ProjectUtils;

import static java.io.File.separator;

public class Project {
    private TemplateProvider templateProvider;
    private ProjectStructure projectStructure;
    private ProjectWallet projectWallet;

    private Project(
            TemplateProvider templateProvider,
            ProjectStructure projectStructure,
            ProjectWallet projectWallet) {
        this.projectStructure = projectStructure;
        this.templateProvider = templateProvider;
        this.projectWallet = projectWallet;
    }

    public TemplateProvider getTemplateProvider() {
        return this.templateProvider;
    }

    public ProjectStructure getProjectStructure() {
        return this.projectStructure;
    }

    public ProjectWallet getProjectWallet() {
        return this.projectWallet;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private File solidityImportPath;
        private boolean withWallet = false;
        private boolean withTests = false;
        private String projectName;
        private String packageName;
        private String rootDirectory;
        private boolean withSampleCode = false;
        private boolean withFatJar = false;
        private String command = "new";
        private ProgressCounter progressCounter = new ProgressCounter(true);
        private ProjectWallet projectWallet;

        public Builder withSolidityFile(final File solidityImportPath) {
            this.solidityImportPath = solidityImportPath;
            return this;
        }

        public Builder withWalletProvider() {
            this.withWallet = true;
            return this;
        }

        public Builder withSampleCode() {
            this.withSampleCode = true;
            return this;
        }

        public Builder withTests() {
            this.withTests = true;
            return this;
        }

        public Builder withFatJar() {
            this.withFatJar = true;
            return this;
        }

        public Builder withCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withRootDirectory(String rootDirectory) {
            this.rootDirectory = rootDirectory;
            return this;
        }

        private void buildGradleProject(final String pathToDirectory)
                throws IOException, InterruptedException {
            if (!isWindows()) {
                setExecutable(pathToDirectory, "gradlew");
                executeBuild(
                        new File(pathToDirectory),
                        new String[] {"bash", "-c", "./gradlew build -q"});
            } else {
                setExecutable(pathToDirectory, "gradlew.bat");
                executeBuild(
                        new File(pathToDirectory),
                        new String[] {"cmd.exe", "/c", "gradlew.bat build -q"});
            }
        }

        private void setExecutable(final String pathToDirectory, final String gradlew) {
            final File f = new File(pathToDirectory + File.separator + gradlew);
            final boolean isExecutable = f.setExecutable(true);
        }

        private boolean isWindows() {
            return System.getProperty("os.name").toLowerCase().startsWith("windows");
        }

        private void executeBuild(final File workingDir, final String[] command)
                throws InterruptedException, IOException {
            executeProcess(workingDir, command);
        }

        private void executeProcess(File workingDir, String[] command)
                throws InterruptedException, IOException {
            int exitCode =
                    new ProcessBuilder(command)
                            .directory(workingDir)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .start()
                            .waitFor();
            if (exitCode != 0) {
                Console.exitError("Could not build project.");
            }
        }

        private void createFatJar(String pathToDirectory) throws IOException, InterruptedException {
            if (!isWindows()) {
                executeProcess(
                        new File(pathToDirectory),
                        new String[] {"bash", "./gradlew", "shadowJar", "-q"});
            } else {
                executeProcess(
                        new File(pathToDirectory),
                        new String[] {"cmd.exe", "/c", "./gradlew.bat shadowJar", "-q"});
            }
        }

        public Project build() throws Exception {
            final ProjectStructure projectStructure =
                    new ProjectStructure(rootDirectory, packageName, projectName);
            final ProjectWriter projectWriter = new ProjectWriter();
            generateTopLevelDirectories(projectStructure);
            TemplateProvider builtTemplateProvider =
                    getTemplateProvider(projectStructure, projectWriter);
            generateFiles(projectStructure, projectWriter, builtTemplateProvider);
            progressCounter.processing("Creating " + projectName);
            buildGradleProject(projectStructure.getProjectRoot());
            if (withTests) {
                generateTests(projectStructure);
            }
            if (withFatJar) {
                createFatJar(projectStructure.getProjectRoot());
            }
            progressCounter.setLoading(false);
            return new Project(builtTemplateProvider, projectStructure, projectWallet);
        }

        private void generateTests(ProjectStructure projectStructure) throws IOException {
            String wrapperPath =
                    String.join(
                            separator,
                            projectStructure.getRootDirectory(),
                            projectName,
                            "build",
                            "generated",
                            "source",
                            "web3j",
                            "main",
                            "java");
            String writePath =
                    String.join(
                            separator,
                            projectStructure.getRootDirectory(),
                            projectName,
                            "src",
                            "test",
                            "java");
            new UnitTestCreator(wrapperPath, writePath).generate();
        }

        private void generateTopLevelDirectories(ProjectStructure projectStructure) {
            projectStructure.createMainDirectory();
            projectStructure.createTestDirectory();
            projectStructure.createSolidityDirectory();
            projectStructure.createWrapperDirectory();
        }

        private void generateFiles(
                ProjectStructure projectStructure,
                ProjectWriter projectWriter,
                TemplateProvider builtTemplateProvider)
                throws IOException {
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getMainJavaClass(),
                    InputVerifier.capitalizeFirstLetter(
                            projectStructure.getProjectName() + ".java"),
                    projectStructure.getMainPath());
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getGradleBuild(),
                    File.separator + "build.gradle",
                    projectStructure.getProjectRoot());
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getGradleSettings(),
                    File.separator + "settings.gradle",
                    projectStructure.getProjectRoot());
            if (solidityImportPath == null) {
                projectWriter.writeResourceFile(
                        builtTemplateProvider.getSolidityProject(),
                        "HelloWorld.sol",
                        projectStructure.getSolidityPath());
            } else {
                projectWriter.importSolidityProject(
                        solidityImportPath, projectStructure.getSolidityPath());
            }
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getGradlewWrapperSettings(),
                    File.separator + "gradle-wrapper.properties",
                    projectStructure.getWrapperPath());
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getGradlewScript(),
                    File.separator + "gradlew",
                    projectStructure.getProjectRoot());
            projectWriter.writeResourceFile(
                    builtTemplateProvider.getGradlewBatScript(),
                    File.separator + "gradlew.bat",
                    projectStructure.getProjectRoot());
            projectWriter.copyResourceFile(
                    builtTemplateProvider.getGradlewJar(),
                    projectStructure.getWrapperPath() + File.separator + "gradle-wrapper.jar");
        }

        private TemplateProvider getTemplateProvider(
                ProjectStructure projectStructure, ProjectWriter projectWriter) throws Exception {
            TemplateProvider.Builder templateProvider =
                    new TemplateProvider.Builder()
                            .loadGradlewBatScript("gradlew.bat.template")
                            .loadGradlewScript("gradlew.template");
            if (command.equals("new")) {
                templateProvider.loadGradleBuild(
                        JavaVersion.getJavaVersionAsDouble() < 11
                                ? "build.gradle.template"
                                : "build.gradleJava11.template");
            } else if (command.equals("import")) {
                templateProvider.loadGradleBuild(
                        JavaVersion.getJavaVersionAsDouble() < 11
                                ? "build.gradleImport.template"
                                : "build.gradleImportJava11.template");
            }
            templateProvider
                    .loadGradleSettings("settings.gradle.template")
                    .loadGradlewWrapperSettings("gradlew-wrapper.properties.template")
                    .loadGradleJar("gradle-wrapper.jar")
                    .loadSolidityGreeter("HelloWorld.sol");
            if (withSampleCode) {
                templateProvider.loadMainJavaClass("Template.java");
            } else {
                templateProvider.loadMainJavaClass("EmptyTemplate.java");
            }
            templateProvider
                    .withPackageNameReplacement(s -> s.replace("<package_name>", packageName))
                    .withProjectNameReplacement(
                            s ->
                                    s.replace(
                                            "<project_name>",
                                            InputVerifier.capitalizeFirstLetter(projectName)));
            if (withWallet) {
                projectStructure.createWalletDirectory();
                projectWallet =
                        new ProjectWallet(
                                ProjectUtils.generateWalletPassword(),
                                projectStructure.getWalletPath());
                templateProvider
                        .withPrivateKeyReplacement(
                                s ->
                                        s.replace(
                                                "<password_file_name>",
                                                projectWallet.getPasswordFileName()))
                        .withWalletNameReplacement(
                                s -> s.replace("<wallet_name>", projectWallet.getWalletName()));
                projectWriter.writeResourceFile(
                        projectWallet.getWalletPassword(),
                        projectWallet.getPasswordFileName(),
                        projectStructure.getWalletPath());
            }
            return templateProvider.build();
        }
    }
}
