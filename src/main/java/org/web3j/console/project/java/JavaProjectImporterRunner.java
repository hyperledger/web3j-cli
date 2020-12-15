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
package org.web3j.console.project.java;

import java.io.File;
import java.util.Optional;

import org.web3j.console.project.ProjectImporterConfig;

public class JavaProjectImporterRunner extends JavaProjectRunner {

    public String solidityImportPath;

    public JavaProjectImporterRunner(final ProjectImporterConfig projectImporterConfig) {
        super(projectImporterConfig);
        solidityImportPath = projectImporterConfig.getSolidityImportPath();
    }

    protected void createProject() {
        generateJava(
                withTests, Optional.of(new File(solidityImportPath)), withJar, false, "import");
    }
}
