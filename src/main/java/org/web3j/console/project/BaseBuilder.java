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

import org.web3j.console.project.java.JavaProjectStructure;

public class BaseBuilder {

    protected String solidityImportPath;
    protected boolean withWallet;
    protected boolean withTests;
    protected String projectName;
    protected String packageName;
    protected String rootDirectory;
    protected boolean withSampleCode;
    protected boolean withFatJar;
    protected String command = "new";

    public <T extends BaseBuilder> BaseBuilder withSolidityFile(final String solidityImportPath) {
        this.solidityImportPath = solidityImportPath;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withWalletProvider(boolean withWalletProvider) {
        this.withWallet = withWalletProvider;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withSampleCode(boolean withSampleCode) {
        this.withSampleCode = withSampleCode;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withTests(boolean withTests) {
        this.withTests = withTests;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withFatJar(boolean withFatJar) {
        this.withFatJar = withFatJar;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withCommand(String command) {
        this.command = command;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public <T extends BaseBuilder> BaseBuilder withRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public <T extends BaseProject> BaseProject build() throws Exception {
        final ProjectStructure projectStructure =
                new JavaProjectStructure(rootDirectory, packageName, projectName);
        return new BaseProject(
                withTests,
                withFatJar,
                withWallet,
                withSampleCode,
                command,
                solidityImportPath,
                projectStructure);
    }
}
