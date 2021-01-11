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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ProjectWriter {

    /**
     * Writes file content into the output directory. Can be used with <code>
     * TemplateReader.readFile()</code> to get the file content from JAR.
     *
     * @param file File content to be written.
     * @param fileName output file name
     * @param writeLocation output location
     * @throws IOException when file is not found
     */
    public static void writeResourceFile(
            final String file, final String fileName, final String writeLocation)
            throws IOException {
        Files.write(Paths.get(writeLocation, fileName), getBytes(file));
    }

    private static byte[] getBytes(final String file) {
        return file.getBytes();
    }

    public static void copyResourceFile(final String file, final String destinationPath)
            throws IOException {

        Files.copy(
                Objects.requireNonNull(
                        ProjectWriter.class.getClassLoader().getResourceAsStream(file)),
                Paths.get(destinationPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public static void importSolidityProject(
            final File solidityImportPath, final String destination) throws IOException {
        if (solidityImportPath != null && solidityImportPath.exists()) {
            if (solidityImportPath.isFile() && solidityImportPath.getName().endsWith(".sol")) {
                Files.copy(
                        solidityImportPath.toPath(),
                        Paths.get(destination, solidityImportPath.getName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.walkFileTree(
                        solidityImportPath.toPath(),
                        new ProjectVisitor(solidityImportPath.getAbsolutePath(), destination));
            }
        }
    }
}
