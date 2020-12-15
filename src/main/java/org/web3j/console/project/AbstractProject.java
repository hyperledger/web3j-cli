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

import java.io.IOException;

import org.web3j.console.project.templates.TemplateProvider;
import org.web3j.console.project.utils.ProgressCounter;
import org.web3j.console.project.utils.ProjectCreationUtils;
import org.web3j.console.project.wallet.ProjectWallet;

public abstract class AbstractProject<T extends AbstractProject<T>> {
    private T project;

    protected final boolean withTests;
    protected final boolean withFatJar;
    protected final boolean withSampleCode;
    protected final String command;
    protected final String solidityImportPath;
    protected final ProjectStructure projectStructure;
    protected ProjectWallet projectWallet;
    protected ProgressCounter progressCounter = new ProgressCounter(true);

    protected abstract T getProjectInstance();

    protected AbstractProject(
            boolean withTests,
            boolean withFatJar,
            boolean withSampleCode,
            String command,
            String solidityImportPath,
            ProjectStructure projectStructure) {
        this.withTests = withTests;
        this.withFatJar = withFatJar;
        this.withSampleCode = withSampleCode;
        this.command = command;
        this.solidityImportPath = solidityImportPath;
        this.projectStructure = projectStructure;
        this.project = getProjectInstance();
    }

    public ProjectStructure getProjectStructure() {
        return project.projectStructure;
    }

    public ProjectWallet getProjectWallet() {
        return project.projectWallet;
    }

    public void createProject() throws IOException, InterruptedException {
        ProjectCreationUtils.generateTopLevelDirectories(projectStructure);
        getTemplateProvider().generateFiles(projectStructure);
        progressCounter.processing(
                "Creating and building project ... Subsequent builds will be faster");
        ProjectCreationUtils.generateWrappers(projectStructure.getProjectRoot());
        if (withTests) {
            generateTests(projectStructure);
        }
        if (withFatJar) {
            ProjectCreationUtils.createFatJar(projectStructure.getProjectRoot());
        }
        progressCounter.setLoading(false);
    }

    protected abstract TemplateProvider getTemplateProvider();

    protected abstract void generateTests(ProjectStructure projectStructure) throws IOException;
}
