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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import static org.web3j.console.project.utils.ProjectUtils.isSmartContract;

public class ProjectVisitor extends SimpleFileVisitor<Path> {
    private final String source;
    private final String destination;

    public ProjectVisitor(final String source, final String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        String filePath = path.toFile().getCanonicalPath();
        String sourcePath = new File(source).getCanonicalPath();

        if (!filePath.startsWith(sourcePath)) {
            throw new IOException("Unsupported source location: " + filePath);
        }

        File destFile =
                new File(destination + File.separator + filePath.substring(sourcePath.length()));

        if (isSmartContract(path.toFile())) {
            if (!destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs()) {
                throw new IOException("Unable to create folder: " + destFile.getParent());
            }
            Files.copy(path, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return FileVisitResult.CONTINUE;
    }
}
