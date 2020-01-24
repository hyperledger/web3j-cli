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
package org.web3j.console.project.templates;

public class TemplateBuilder<T extends TemplateBuilder<T>> {
    protected String mainJavaClass;
    protected String gradleBuild;
    protected String gradleSettings;
    protected String gradlewWrapperSettings;
    protected String gradlewBatScript;
    protected String gradlewScript;
    protected String solidityProject;
    protected String gradlewWrapperJar;
    protected String packageNameReplacement;
    protected String projectNameReplacement;
    protected String passwordFileName;
    protected String walletNameReplacement;
    protected String pathToSolidityFolder;

    public TemplateBuilder withMainJavaClass(String mainJavaClass) {
        this.mainJavaClass = mainJavaClass;
        return this;
    }

    public TemplateBuilder withGradleBuild(String gradleBuild) {
        this.gradleBuild = gradleBuild;
        return this;
    }

    public TemplateBuilder withGradleSettings(String gradleSettings) {
        this.gradleSettings = gradleSettings;
        return this;
    }

    public TemplateBuilder withWrapperGradleSettings(String gradlewWrapperSettings) {
        this.gradlewWrapperSettings = gradlewWrapperSettings;
        return this;
    }

    public TemplateBuilder withGradleBatScript(String gradlewBatScript) {
        this.gradlewBatScript = gradlewBatScript;
        return this;
    }

    public TemplateBuilder withGradleScript(String gradlewScript) {
        this.gradlewScript = gradlewScript;
        return this;
    }

    public TemplateBuilder withGradlewWrapperJar(String gradlewWrapperJar) {
        this.gradlewWrapperJar = gradlewWrapperJar;
        return this;
    }

    public TemplateBuilder withSolidityProject(String solidityProject) {
        this.solidityProject = solidityProject;
        return this;
    }

    public TemplateBuilder withPathToSolidityFolder(String pathToSolidityFolder) {
        this.pathToSolidityFolder = pathToSolidityFolder;
        return this;
    }

    public TemplateBuilder withPackageNameReplacement(String packageNameReplacement) {
        this.packageNameReplacement = packageNameReplacement;
        return this;
    }

    public TemplateBuilder withProjectNameReplacement(String projectNameReplacement) {
        this.projectNameReplacement = projectNameReplacement;
        return this;
    }

    public TemplateBuilder withPasswordFileName(String passwordFileName) {
        this.passwordFileName = passwordFileName;
        return this;
    }

    public TemplateBuilder withWalletNameReplacement(String walletNameReplacement) {
        this.walletNameReplacement = walletNameReplacement;
        return this;
    }

    public <T extends TemplateProvider> TemplateProvider build() {
        return new TemplateProvider(
                mainJavaClass,
                solidityProject,
                pathToSolidityFolder,
                gradleBuild,
                gradleSettings,
                gradlewWrapperSettings,
                gradlewBatScript,
                gradlewScript,
                gradlewWrapperJar,
                packageNameReplacement,
                projectNameReplacement,
                passwordFileName,
                walletNameReplacement);
    }
}
