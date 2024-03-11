/*
 * Copyright 2024 Web3 Labs Ltd.
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
import org.web3j.console.project.Erc721ProjectCreatorConfig;
import org.web3j.console.project.ProjectRunner;
import org.web3j.console.project.templates.java.erc721.Erc721JavaTemplateBuilder;
import org.web3j.console.project.utils.ProgressCounter;
import org.web3j.console.project.utils.ProjectCreationUtils;

public class Erc721JavaProjectCreator extends ProjectRunner {

    Erc721ProjectCreatorConfig erc721ProjectCreatorConfig;

    public Erc721JavaProjectCreator(Erc721ProjectCreatorConfig erc721ProjectCreatorConfig) {
        super(erc721ProjectCreatorConfig);
        this.erc721ProjectCreatorConfig = erc721ProjectCreatorConfig;
    }

    @Override
    protected void createProject() {
        ProgressCounter progressCounter = new ProgressCounter(true);
        progressCounter.processing(
                "Creating and building ERC721 project ... Subsequent builds will be faster");
        JavaProjectStructure projectStructure =
                new JavaProjectStructure(outputDir, packageName, projectName);
        ProjectCreationUtils.generateTopLevelDirectories(projectStructure);
        try {
            new Erc721JavaTemplateBuilder()
                    .withTokenName(erc721ProjectCreatorConfig.getTokenName())
                    .withTokenSymbol(erc721ProjectCreatorConfig.getTokenSymbol())
                    .withProjectNameReplacement(projectName)
                    .withPackageNameReplacement(packageName)
                    .withGradleBatScript("project/gradlew.bat.template")
                    .withGradleScript("project/gradlew.template")
                    .withGradleSettings("project/settings.gradle.template")
                    .withWrapperGradleSettings("project/gradlew-wrapper.properties.template")
                    .withGradlewWrapperJar("gradle-wrapper.jar")
                    .withGradleBuild("project/erc721/build.gradleErc721.template")
                    .withMainJavaClass("project/erc721/JavaErc721.template")
                    .build()
                    .generateFiles(projectStructure);

            CopyUtils.INSTANCE.copyFromResources(
                    "contracts/ERC721Token.sol", projectStructure.getSolidityPath());
            buildProject(projectStructure, progressCounter);
        } catch (Exception e) {
            e.printStackTrace(SimpleFileLogger.INSTANCE.getFilePrintStream());
            PrettyPrinter.INSTANCE.onFailed();
            System.exit(1);
        }
    }
}
