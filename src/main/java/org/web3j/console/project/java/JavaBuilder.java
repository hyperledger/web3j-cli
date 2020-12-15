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
package org.web3j.console.project.java;

import org.web3j.console.project.AbstractProjectBuilder;
import org.web3j.console.project.ProjectBuilder;
import org.web3j.console.project.ProjectStructure;

public class JavaBuilder extends AbstractProjectBuilder<JavaBuilder> implements ProjectBuilder {

    protected String command = "new";

    public JavaBuilder() {
        super();
    }

    public JavaBuilder withCommand(String command) {
        this.command = command;
        return this;
    }

    public JavaProject build() {
        final ProjectStructure projectStructure =
                new JavaProjectStructure(rootDirectory, packageName, projectName);
        return new JavaProject(
                withTests,
                withFatJar,
                withSampleCode,
                command,
                solidityImportPath,
                projectStructure);
    }

    protected JavaBuilder getBuilderInstance() {
        return this;
    }
}
