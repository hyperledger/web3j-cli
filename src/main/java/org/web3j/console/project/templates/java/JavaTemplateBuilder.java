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

import org.web3j.console.project.templates.TemplateBuilder;

public class JavaTemplateBuilder implements TemplateBuilder {

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
    protected String pathToSolidityFolder;

    public JavaTemplateBuilder withMainJavaClass(String mainJavaClass) {
        this.mainJavaClass = mainJavaClass;
        return this;
    }

    public JavaTemplateBuilder withGradleBuild(String gradleBuild) {
        this.gradleBuild = gradleBuild;
        return this;
    }

    public JavaTemplateBuilder withGradleSettings(String gradleSettings) {
        this.gradleSettings = gradleSettings;
        return this;
    }

    public JavaTemplateBuilder withWrapperGradleSettings(String gradlewWrapperSettings) {
        this.gradlewWrapperSettings = gradlewWrapperSettings;
        return this;
    }

    public JavaTemplateBuilder withGradleBatScript(String gradlewBatScript) {
        this.gradlewBatScript = gradlewBatScript;
        return this;
    }

    public JavaTemplateBuilder withGradleScript(String gradlewScript) {
        this.gradlewScript = gradlewScript;
        return this;
    }

    public JavaTemplateBuilder withGradlewWrapperJar(String gradlewWrapperJar) {
        this.gradlewWrapperJar = gradlewWrapperJar;
        return this;
    }

    public JavaTemplateBuilder withSolidityProject(String solidityProject) {
        this.solidityProject = solidityProject;
        return this;
    }

    public JavaTemplateBuilder withPathToSolidityFolder(String pathToSolidityFolder) {
        this.pathToSolidityFolder = pathToSolidityFolder;
        return this;
    }

    public JavaTemplateBuilder withPackageNameReplacement(String packageNameReplacement) {
        this.packageNameReplacement = packageNameReplacement;
        return this;
    }

    public JavaTemplateBuilder withProjectNameReplacement(String projectNameReplacement) {
        this.projectNameReplacement = projectNameReplacement;
        return this;
    }

    public JavaTemplateProvider build() {
        return new JavaTemplateProvider(
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
                null);
    }
}
