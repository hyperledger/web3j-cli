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
import org.web3j.console.openapi.utils.PrettyPrinter
import org.web3j.console.Web3jVersionProvider
import org.web3j.console.project.utils.ProgressCounter
import org.web3j.console.project.utils.ProjectUtils.exitIfNoContractFound

import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File

@Command(
    name = "import",
    description = ["Import existing Solidity contracts into a new Web3j-OpenAPI Project."],
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
class ImportOpenApiCommand : AbstractOpenApiCommand() {

    @Option(
        names = ["-s", "--solidity-path"],
        description = ["Path to Solidity file/folder"]
    )
    var solidityImportPath: String? = null

    override fun generate(projectFolder: File) {
        if (solidityImportPath == null) {
            solidityImportPath = interactiveOptions.solidityProjectPath
        }
        exitIfNoContractFound(File(solidityImportPath!!))

        val progressCounter = ProgressCounter(true)
        progressCounter.processing("Creating and Building ${projectOptions.projectName} project ... Subsequent builds will be faster")

        val projectStructure = createProjectStructure(
            openApiTemplateProvider = OpenApiTemplateProvider(
                solidityContract = "",
                pathToSolidityFolder = solidityImportPath!!,
                gradleBuild = "project/build.gradleImportOpenApi.template",
                packageName = projectOptions.packageName,
                projectName = projectOptions.projectName,
                contextPath = contextPath,
                addressLength = (projectOptions.addressLength * 8).toString()
            ), outputDir = projectOptions.outputDir)

        buildProject(projectStructure.projectRoot, withSwaggerUi = false)

        progressCounter.setLoading(false)
        PrettyPrinter.onOpenApiProjectSuccess()
    }
}
