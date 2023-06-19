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

import org.web3j.console.openapi.project.erc777.CopyUtils;
import org.web3j.console.openapi.utils.PrettyPrinter;
import org.web3j.console.openapi.utils.SimpleFileLogger;
import org.web3j.console.project.Erc20ProjectCreatorConfig;
import org.web3j.console.project.ProjectRunner;
import org.web3j.console.project.templates.java.erc20.Erc20JavaTemplateBuilder;
import org.web3j.console.project.utils.ProgressCounter;
import org.web3j.console.project.utils.ProjectCreationUtils;

public class Erc20JavaProjectCreator extends ProjectRunner {

    Erc20ProjectCreatorConfig erc20ProjectCreatorConfig;

    public Erc20JavaProjectCreator(Erc20ProjectCreatorConfig erc20ProjectCreatorConfig) {
        super(erc20ProjectCreatorConfig);
        this.erc20ProjectCreatorConfig = erc20ProjectCreatorConfig;
    }

    @Override
    protected void createProject() {
        ProgressCounter progressCounter = new ProgressCounter(true);
        progressCounter.processing(
                "Creating and building ERC20 project ... Subsequent builds will be faster");
        JavaProjectStructure projectStructure =
                new JavaProjectStructure(outputDir, packageName, projectName);
        ProjectCreationUtils.generateTopLevelDirectories(projectStructure);
        try {
            new Erc20JavaTemplateBuilder()
                    .withTokenName(erc20ProjectCreatorConfig.getTokenName())
                    .withTokenSymbol(erc20ProjectCreatorConfig.getTokenSymbol())
                    .withInitialSupply(erc20ProjectCreatorConfig.getInitialSupply())
                    .withProjectNameReplacement(projectName)
                    .withPackageNameReplacement(packageName)
                    .withGradleBatScript("project/gradlew.bat.template")
                    .withGradleScript("project/gradlew.template")
                    .withGradleSettings("project/settings.gradle.template")
                    .withWrapperGradleSettings("project/gradlew-wrapper.properties.template")
                    .withGradlewWrapperJar("gradle-wrapper.jar")
                    .withGradleBuild("project/erc20/build.gradleErc20.template")
                    .withMainJavaClass("project/erc20/JavaErc20.template")
                    .build()
                    .generateFiles(projectStructure);

            CopyUtils.INSTANCE.copyFromResources(
                    "contracts/ERC20Token.sol", projectStructure.getSolidityPath());
            buildProject(projectStructure, progressCounter);
        } catch (Exception e) {
            e.printStackTrace(SimpleFileLogger.INSTANCE.getFilePrintStream());
            PrettyPrinter.INSTANCE.onFailed();
            System.exit(1);
        }
    }
}
