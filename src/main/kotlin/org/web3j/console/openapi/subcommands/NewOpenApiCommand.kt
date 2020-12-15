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
package org.web3j.console.openapi.subcommands

import org.web3j.console.openapi.project.OpenApiProjectCreationUtils.buildProject
import org.web3j.console.openapi.project.OpenApiProjectCreationUtils.createProjectStructure
import org.web3j.console.openapi.project.OpenApiTemplateProvider
import org.web3j.console.openapi.project.erc777.CopyUtils
import org.web3j.console.openapi.utils.PrettyPrinter

import org.web3j.console.Web3jVersionProvider
import org.web3j.console.project.TemplateType
import org.web3j.console.project.utils.ProgressCounter
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File

@Command(
    name = "new",
    description = ["Create a new Web3j-OpenAPI project."],
    abbreviateSynopsis = true,
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    versionProvider = Web3jVersionProvider::class,
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    optionListHeading = "%nOptions:%n",
    footerHeading = "%n",
    footer = ["Epirus CLI is licensed under the Apache License 2.0"]
)
class NewOpenApiCommand : AbstractOpenApiCommand() {

    @Parameters(description = ["HelloWorld, ERC20, ERC777"], defaultValue = "HelloWorld")
    var templateType = TemplateType.HelloWorld

    override fun generate(projectFolder: File) {
        val progressCounter = ProgressCounter(true)
        progressCounter.processing("Creating and Building ${projectOptions.projectName} project ... Subsequent builds will be faster")

        when (templateType) {
            TemplateType.HelloWorld -> {
                val projectStructure = createProjectStructure(
                    openApiTemplateProvider = OpenApiTemplateProvider(
                        solidityContract = "contracts/HelloWorld.sol",
                        pathToSolidityFolder = "",
                        gradleBuild = "project/build.gradleOpenApi.template",
                        packageName = projectOptions.packageName,
                        projectName = projectOptions.projectName,
                        contextPath = contextPath,
                        addressLength = (projectOptions.addressLength * 8).toString()
                    ), outputDir = projectOptions.outputDir
                )
                buildProject(projectStructure.projectRoot, withSwaggerUi = false)
            }

            TemplateType.ERC777 -> {
                val projectStructure = createProjectStructure(
                    openApiTemplateProvider = OpenApiTemplateProvider(
                        solidityContract = "",
                        pathToSolidityFolder = "",
                        gradleBuild = "project/erc777/build.gradleOpenApiErc777.template",
                        packageName = projectOptions.packageName,
                        projectName = projectOptions.projectName,
                        contextPath = contextPath,
                        addressLength = (projectOptions.addressLength * 8).toString()
                    ), outputDir = projectOptions.outputDir
                )
                CopyUtils.copyFromResources(
                    "contracts/ERC777Token.sol",
                    projectStructure.solidityPath)
                buildProject(projectStructure.projectRoot, withSwaggerUi = false)
            }

            TemplateType.ERC20 -> {
                val projectStructure = createProjectStructure(
                    openApiTemplateProvider = OpenApiTemplateProvider(
                        solidityContract = "",
                        pathToSolidityFolder = "",
                        gradleBuild = "project/erc20/build.gradleOpenApiErc20.template",
                        packageName = projectOptions.packageName,
                        projectName = projectOptions.projectName,
                        contextPath = contextPath,
                        addressLength = (projectOptions.addressLength * 8).toString()
                    ), outputDir = projectOptions.outputDir
                )
                CopyUtils.copyFromResources(
                    "contracts/ERC20Token.sol",
                    projectStructure.solidityPath)
                buildProject(projectStructure.projectRoot, withSwaggerUi = false)
            }
        }

        progressCounter.setLoading(false)
        PrettyPrinter.onOpenApiProjectSuccess()
    }
}
