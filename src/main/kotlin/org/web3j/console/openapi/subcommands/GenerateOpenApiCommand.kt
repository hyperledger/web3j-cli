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

import org.apache.commons.io.FileUtils
import org.web3j.console.Web3jVersionProvider
import org.web3j.console.project.utils.ProgressCounter
import org.web3j.console.project.utils.ProjectUtils.deleteFolder
import org.web3j.console.project.utils.ProjectUtils.exitIfNoContractFound
import picocli.CommandLine.Option
import picocli.CommandLine.Command
import picocli.CommandLine.Help.Visibility.ALWAYS
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Command(
        name = "generate",
        description = ["Generate REST endpoints from existing Solidity contracts."],
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider::class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = ["Web3j CLI is licensed under the Apache License 2.0"])
class GenerateOpenApiCommand : AbstractOpenApiCommand() {

    @Option(
        names = ["-s", "--solidity-path"],
        description = ["Path to Solidity file/folder"]
    )
    var solidityImportPath: String? = null

    @Option(
        names = ["--with-implementations"],
        description = ["Generate the interfaces implementations."],
        showDefaultValue = ALWAYS
    )
    var withImplementations: Boolean = false

    override fun generate(projectFolder: File) {
        if (solidityImportPath == null) {
            solidityImportPath = interactiveOptions.solidityProjectPath
        }
        exitIfNoContractFound(File(solidityImportPath!!))

        val progressCounter = ProgressCounter(true)
        progressCounter.processing("Generating REST endpoints ...")

        val projectFolderName = "GenerateEndpoints"
        val tempFolder = Files.createTempDirectory(Paths.get(projectOptions.outputDir), projectFolderName)

        val projectStructure = createProjectStructure(
            openApiTemplateProvider = OpenApiTemplateProvider(
                solidityContract = "",
                pathToSolidityFolder = solidityImportPath!!,
                gradleBuild = "project/build.gradleGenerateOpenApi.template",
                packageName = projectOptions.packageName,
                projectName = projectOptions.projectName,
                contextPath = contextPath,
                addressLength = (projectOptions.addressLength * 8).toString(),
                generateServer = withImplementations.toString()
            ), outputDir = tempFolder.toAbsolutePath().toString())

        buildProject(
            projectStructure.projectRoot,
            withOpenApi = true,
            withSwaggerUi = false,
            withShadowJar = false)

        FileUtils.copyDirectory(Paths.get(tempFolder.toString(), projectOptions.projectName, "build", "generated", "sources", "web3j", "main").toFile(),
            Paths.get(projectOptions.outputDir, projectOptions.projectName).toFile())

        deleteFolder(tempFolder)
        progressCounter.setLoading(false)
        PrettyPrinter.onSuccess()
    }
}
