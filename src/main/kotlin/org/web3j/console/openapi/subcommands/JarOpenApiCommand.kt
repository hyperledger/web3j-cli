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
import org.web3j.console.project.utils.ProjectUtils.findSolidityContracts

import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Command(
    name = "jar",
    description = ["Generate an executable Web3j-OpenAPI JAR."],
    abbreviateSynopsis = true,
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    versionProvider = Web3jVersionProvider::class,
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    optionListHeading = "%nOptions:%n",
    footerHeading = "%n",
    footer = ["Web3j CLI is licensed under the Apache License 2.0"]
)
class JarOpenApiCommand : AbstractOpenApiCommand() {

    @Option(
        names = ["-s", "--solidity-path"],
        description = ["Path to Solidity file/folder"]
    )
    var solidityImportPath: String? = null

    /**
     * Path to the `.web3j` folder
     */
    private val web3jHomeFolder: Path =
        Paths.get(System.getenv(if (System.getProperty("os.name").lowercase().startsWith("win")) "USERPROFILE" else "HOME"), ".web3j")

    override fun generate(projectFolder: File) {
        if (solidityImportPath == null) {
            solidityImportPath = interactiveOptions.solidityProjectPath
        }
        exitIfNoContractFound(File(solidityImportPath!!))

        val progressCounter = ProgressCounter(true)
        progressCounter.processing("Creating and Building ${projectOptions.projectName} JAR ... Subsequent builds will be faster")

        Paths.get(projectFolder.toString(), projectOptions.projectName)

        val projectDirectoryPath = getCachedDirectoryPath(findSolidityContracts(Paths.get(solidityImportPath!!)))
        if (!projectDirectoryPath.toFile().exists()) {
            createProjectStructure(
                openApiTemplateProvider = OpenApiTemplateProvider(
                    solidityContract = "",
                    pathToSolidityFolder = solidityImportPath!!,
                    gradleBuild = "project/build.gradleJarOpenApi.template",
                    packageName = projectOptions.packageName,
                    projectName = projectOptions.projectName,
                    contextPath = contextPath,
                    addressLength = (projectOptions.addressLength * 8).toString()
                ), outputDir = projectDirectoryPath.toString())
        }
        buildProject(
            Paths.get(projectDirectoryPath.toString(), projectOptions.projectName).toString(),
            withOpenApi = false,
            withSwaggerUi = false,
            withShadowJar = true)

        Files.copy(
            getJarFile(projectDirectoryPath),
            File(projectOptions.outputDir, "${projectOptions.projectName}$JAR_SUFFIX").toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )

        progressCounter.setLoading(false)
        PrettyPrinter.onJarSuccess()
    }

    /**
     * Returns the path to the generated Jar when applying the Shadow plugin to the project.
     *
     * @param outputProjectFolder The cached directory containing the project
     * @return Path to the Jar
     */
    private fun getJarFile(outputProjectFolder: Path): Path {
        return File(outputProjectFolder
                .resolve(projectOptions.projectName)
                .resolve("build")
                .resolve("libs")
                .toString()
        ).listFiles()!!.first { it.name.endsWith("-all.jar") }.toPath()
    }

    /**
     * Creates the Path of the project directory used to generate the JAR.
     * This latter is in <code>~/.epirus/projects/HashCode(contractName1, contractName2, ...)</code>
     *
     * @param contracts List of contracts that will be used in the project
     * @return Path of the cached directory
     */
    private fun getCachedDirectoryPath(contracts: List<Path>): Path {
        val folderNameHashCode = contracts.sorted().joinToString("") {
            it.toFile().nameWithoutExtension
        }.hashCode().toString()
        return web3jHomeFolder.resolve("projects").resolve(folderNameHashCode)
    }
}
