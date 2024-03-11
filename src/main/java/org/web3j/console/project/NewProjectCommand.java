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
package org.web3j.console.project;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.project.java.Erc20JavaProjectCreator;
import org.web3j.console.project.java.Erc721JavaProjectCreator;
import org.web3j.console.project.java.Erc777JavaProjectCreator;
import org.web3j.console.project.java.JavaProjectCreatorRunner;
import org.web3j.console.project.kotlin.KotlinProjectCreatorRunner;

@Command(
        name = "new",
        description = "Create a new Web3j Project",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class NewProjectCommand extends AbstractProjectCommand implements Runnable {

    @Parameters(description = "HelloWorld, ERC20, ERC777, ERC721", defaultValue = "HelloWorld")
    TemplateType templateType = TemplateType.HelloWorld;

    @Override
    public void run() {
        setupProject();
        final ProjectCreatorConfig projectCreatorConfig =
                new ProjectCreatorConfig(
                        projectOptions.projectName,
                        projectOptions.packageName,
                        projectOptions.outputDir,
                        projectOptions.generateJar,
                        projectOptions.generateTests);

        if (projectOptions.isKotlin) {
            switch (templateType) {
                case HelloWorld:
                    new KotlinProjectCreatorRunner(projectCreatorConfig).run();
                    break;
                case ERC20:
                    System.out.println("Generating ERC20 Kotlin project is currently unsupported");
                    break;
                case ERC777:
                    System.out.println("Generating ERC777 Kotlin project is currently unsupported");
                    break;
                case ERC721:
                    System.out.println("Generating ERC721 Kotlin project is currently unsupported");
                    break;
            }
        } else {
            switch (templateType) {
                case HelloWorld:
                    new JavaProjectCreatorRunner(projectCreatorConfig).run();
                    break;
                case ERC777:
                    new Erc777JavaProjectCreator(
                                    new Erc777ProjectCreatorConfig(
                                            projectOptions.projectName,
                                            projectOptions.packageName,
                                            projectOptions.outputDir,
                                            projectOptions.generateJar,
                                            projectOptions.generateTests,
                                            interactiveOptions.getTokenName("ERC777"),
                                            interactiveOptions.getTokenSymbol("erc777"),
                                            interactiveOptions.getTokenInitialSupply("1000000000"),
                                            interactiveOptions.getTokenDefaultOperators()))
                            .run();
                    break;
                case ERC20:
                    new Erc20JavaProjectCreator(
                                    new Erc20ProjectCreatorConfig(
                                            projectOptions.projectName,
                                            projectOptions.packageName,
                                            projectOptions.outputDir,
                                            projectOptions.generateJar,
                                            projectOptions.generateTests,
                                            interactiveOptions.getTokenName("ERC20"),
                                            interactiveOptions.getTokenSymbol("erc20"),
                                            interactiveOptions.getTokenInitialSupply("1000000000")))
                            .run();
                    break;
                case ERC721:
                    new Erc721JavaProjectCreator(
                                    new Erc721ProjectCreatorConfig(
                                            projectOptions.projectName,
                                            projectOptions.packageName,
                                            projectOptions.outputDir,
                                            projectOptions.generateJar,
                                            projectOptions.generateTests,
                                            interactiveOptions.getTokenName("ERC721"),
                                            interactiveOptions.getTokenSymbol("erc721")))
                            .run();
                    break;
            }
        }
    }
}
