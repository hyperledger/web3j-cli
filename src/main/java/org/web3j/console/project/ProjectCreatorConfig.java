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
package org.web3j.console.project;

public class ProjectCreatorConfig {
    private final String projectName;
    private final String packageName;
    private final String outputDir;
    private final Boolean withJar;
    private final Boolean withTests;

    public ProjectCreatorConfig(
            final String projectName,
            final String packageName,
            final String outputDir,
            final Boolean withJar,
            final Boolean withTests) {

        this.projectName = projectName;
        this.packageName = packageName;
        this.outputDir = outputDir;
        this.withJar = withJar;
        this.withTests = withTests;
    }

    public ProjectCreatorConfig(
            final String projectName, final String packageName, final String outputDir) {

        this.projectName = projectName;
        this.packageName = packageName;
        this.outputDir = outputDir;
        this.withJar = false;
        this.withTests = true;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public Boolean getWithJar() {
        return withJar;
    }

    public Boolean getWithTests() {
        return withTests;
    }
}
