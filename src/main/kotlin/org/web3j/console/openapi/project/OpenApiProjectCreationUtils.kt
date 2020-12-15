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

import org.web3j.console.openapi.project.OpenApiProjectGradleCommands.generateOpenApi
import org.web3j.console.openapi.project.OpenApiProjectGradleCommands.generateOpenApiAndSwaggerUi
import org.web3j.console.openapi.project.OpenApiProjectGradleCommands.generateShadowJar
import org.web3j.console.project.ProjectStructure
import org.web3j.console.project.utils.ProjectCreationUtils

internal object OpenApiProjectCreationUtils {

    /**
     * Creates a new OpenAPI project structure from a set of  contracts.
     *
     * @param openApiTemplateProvider: is the OpenApiTemplateProvider containing all parameters for the generation
     * @param outputDir: project output directory
     *
     * @return The project structure containing all the project directory needed details
     */
    fun createProjectStructure(openApiTemplateProvider: OpenApiTemplateProvider, outputDir: String): ProjectStructure {
        return OpenApiProjectStructure(
            outputDir,
            openApiTemplateProvider.packageName,
            openApiTemplateProvider.projectName
        ).apply {
            ProjectCreationUtils.generateTopLevelDirectories(this)
            openApiTemplateProvider.generateFiles(this)
        }
    }

    /**
     * Runs the necessary gradle tasks to have a working project.
     *
     * @param projectRoot: the project root directory containing the gradle executables
     * @param withOpenApi: generate OpenAPI endpoints
     * @param withSwaggerUi: generate SwaggerUI for the generated endpoints
     * @param withShadowJar: generate an application Jar
     */
    fun buildProject(projectRoot: String, withOpenApi: Boolean = true, withSwaggerUi: Boolean = true, withShadowJar: Boolean = false) {
        if (withOpenApi && withSwaggerUi) {
            generateOpenApiAndSwaggerUi(projectRoot)
        }
        if (withOpenApi && !withSwaggerUi) {
            generateOpenApi(projectRoot)
        }
        if (withShadowJar) {
            generateShadowJar(projectRoot)
        }
    }
}
