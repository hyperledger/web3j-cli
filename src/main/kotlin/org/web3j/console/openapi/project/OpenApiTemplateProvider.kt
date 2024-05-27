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
package org.web3j.console.openapi.project

import org.web3j.console.project.ProjectStructure
import org.web3j.console.project.ProjectWriter
import org.web3j.console.project.templates.TemplateProvider
import org.web3j.console.project.templates.TemplateReader
import java.io.File

class OpenApiTemplateProvider @JvmOverloads constructor(
    private val solidityContract: String,
    private val pathToSolidityFolder: String,
    private val gradleBuild: String,
    val packageName: String,
    val projectName: String,
    private val contextPath: String,
    private val addressLength: String,
    private val generateServer: String = "true",
    private val readme: String = "project/README.openapi.md",
    private val gradleSettings: String = "project/settings.gradle.template",
    private val gradlewWrapperSettings: String = "project/gradlew-wrapper.properties.template",
    private val gradlewBatScript: String = "project/gradlew.bat.template",
    private val gradlewScript: String = "project/gradlew.template",
    private val gradlewJar: String = "gradle-wrapper.jar",
) : TemplateProvider {
    private fun loadGradleBuild(): String {
        return TemplateReader.readFile(gradleBuild)
            .replace("<package_name>".toRegex(), packageName)
            .replace("<project_name>".toRegex(), projectName)
            .replace("<context_path>".toRegex(), contextPath)
            .replace("<address_length>".toRegex(), addressLength)
            .replace("<generate_server>".toRegex(), generateServer)
    }

    fun loadSolidityContract(): String {
        return TemplateReader.readFile(solidityContract)
    }

    private fun loadGradleSettings(): String {
        return TemplateReader.readFile(gradleSettings)
            .replace("<project_name>".toRegex(), projectName)
    }

    private fun loadGradlewWrapperSettings(): String {
        return TemplateReader.readFile(gradlewWrapperSettings)
    }

    fun loadGradlewBatScript(): String {
        return TemplateReader.readFile(gradlewBatScript)
    }

    private fun loadGradlewScript(): String {
        return TemplateReader.readFile(gradlewScript)
    }

    override fun generateFiles(projectStructure: ProjectStructure) {
        ProjectWriter.writeResourceFile(
            loadGradleBuild(),
            "build.gradle",
            projectStructure.projectRoot,
        )
        ProjectWriter.writeResourceFile(
            loadGradleSettings(),
            "settings.gradle",
            projectStructure.projectRoot,
        )
        if (solidityContract.isNotEmpty()) {
            ProjectWriter.writeResourceFile(
                loadSolidityContract(),
                "HelloWorld.sol",
                projectStructure.solidityPath,
            )
        }
        if (pathToSolidityFolder.isNotEmpty()) {
            ProjectWriter.importSolidityProject(
                File(pathToSolidityFolder),
                projectStructure.solidityPath,
            )
        }
        ProjectWriter.writeResourceFile(
            TemplateReader.readFile("project/Dockerfile.template"),
            "Dockerfile",
            projectStructure.projectRoot,
        )
        ProjectWriter.writeResourceFile(
            loadGradlewWrapperSettings(),
            "gradle-wrapper.properties",
            projectStructure.wrapperPath,
        )
        ProjectWriter.writeResourceFile(
            loadGradlewScript(),
            "gradlew",
            projectStructure.projectRoot,
        )
        ProjectWriter.writeResourceFile(
            loadGradlewBatScript(),
            "gradlew.bat",
            projectStructure.projectRoot,
        )
        ProjectWriter.copyResourceFile(
            gradlewJar,
            projectStructure.wrapperPath + "gradle-wrapper.jar",
        )
        ProjectWriter.copyResourceFile(
            readme,
            projectStructure.projectRoot + File.separator + "README.md",
        )
    }
}
