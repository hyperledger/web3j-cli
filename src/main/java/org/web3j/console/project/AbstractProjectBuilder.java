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

public abstract class AbstractProjectBuilder<T extends AbstractProjectBuilder<T>> {
    private T builder;

    protected String solidityImportPath;
    protected boolean withTests;
    protected String projectName;
    protected String packageName;
    protected String rootDirectory;
    protected boolean withSampleCode;
    protected boolean withFatJar;

    protected abstract T getBuilderInstance();

    protected AbstractProjectBuilder() {
        this.builder = getBuilderInstance();
    }

    public T withSolidityFile(final String solidityImportPath) {
        builder.solidityImportPath = solidityImportPath;
        return builder;
    }

    public T withSampleCode(boolean withSampleCode) {
        builder.withSampleCode = withSampleCode;
        return this.builder;
    }

    public T withTests(boolean withTests) {
        builder.withTests = withTests;
        return this.builder;
    }

    public T withFatJar(boolean withFatJar) {
        builder.withFatJar = withFatJar;
        return this.builder;
    }

    public T withProjectName(String projectName) {
        builder.projectName = projectName;
        return this.builder;
    }

    public T withPackageName(String packageName) {
        builder.packageName = packageName;
        return this.builder;
    }

    public T withRootDirectory(String rootDirectory) {
        builder.rootDirectory = rootDirectory;
        return this.builder;
    }
}
