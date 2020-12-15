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
package org.web3j.console.project.kotlin;

import org.web3j.console.project.AbstractProjectBuilder;
import org.web3j.console.project.ProjectBuilder;
import org.web3j.console.project.ProjectStructure;

public class KotlinBuilder extends AbstractProjectBuilder<KotlinBuilder> implements ProjectBuilder {
    protected String command = "new";

    public KotlinBuilder withCommand(String command) {
        this.command = command;
        return this;
    }

    public KotlinProject build() {
        final ProjectStructure projectStructure =
                new KotlinProjectStructure(rootDirectory, packageName, projectName);
        return new KotlinProject(
                withTests,
                withFatJar,
                withSampleCode,
                command,
                solidityImportPath,
                projectStructure);
    }

    @Override
    protected KotlinBuilder getBuilderInstance() {
        return this;
    }
}
