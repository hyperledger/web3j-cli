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
package org.web3j.console.project.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jetty.io.RuntimeIOException;

import static java.io.File.separator;

public class Folders {
    private static List<File> filesToCleanUp = new ArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Folders::removeGeneratedTestFiles));
    }

    private static void removeGeneratedTestFiles() {
        filesToCleanUp.forEach(
                f -> {
                    try {
                        System.out.printf("Removing temporary test directory: %s\n", f.getName());
                        FileUtils.deleteDirectory(f);
                    } catch (IOException ignored) {
                    }
                });
    }

    public static File tempBuildFolder() {
        File tmpTestLocation =
                new File(
                        String.join(
                                separator,
                                "build",
                                "tmp",
                                "testing",
                                String.format(
                                        "%d%s",
                                        System.currentTimeMillis(),
                                        RandomStringUtils.random(10, true, true))));
        if (!tmpTestLocation.mkdirs())
            throw new RuntimeIOException(
                    "Unable to create folder at " + tmpTestLocation.getAbsolutePath());
        filesToCleanUp.add(tmpTestLocation);
        return tmpTestLocation;
    }
}
