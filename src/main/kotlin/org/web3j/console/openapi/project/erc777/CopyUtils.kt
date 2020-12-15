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
package org.web3j.console.openapi.project.erc777

import org.web3j.console.project.ProjectWriter
import org.web3j.console.project.templates.TemplateReader
import java.io.File
import java.io.IOException

internal object CopyUtils {
    /**
     * Copies dependencies from a file to an output file or directory.
     * Used when loading a file from jar resources.
     *
     * @param inputPath relative resource path
     * @param outputPath output directory
     */
    @Throws(IOException::class)
    fun copyFromResources(inputPath: String, outputPath: String) {
        ProjectWriter.writeResourceFile(
            TemplateReader.readFile(inputPath),
            File(inputPath).name,
            outputPath
        )
    }
}
