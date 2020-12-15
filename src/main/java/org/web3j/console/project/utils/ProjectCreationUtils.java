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

import org.web3j.console.openapi.utils.PrettyPrinter;
import org.web3j.console.openapi.utils.SimpleFileLogger;
import org.web3j.console.project.ProjectStructure;

public class ProjectCreationUtils {
    public static void generateWrappers(final String pathToDirectory)
            throws IOException, InterruptedException {
        if (!isWindows()) {
            setExecutable(pathToDirectory, "gradlew");
            executeBuild(
                    new File(pathToDirectory),
                    new String[] {"bash", "-c", "./gradlew generateContractWrappers -q"});
        } else {
            setExecutable(pathToDirectory, "gradlew.bat");
            executeBuild(
                    new File(pathToDirectory),
                    new String[] {"cmd", "/c", ".\\gradlew.bat generateContractWrappers -q"});
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public static void setExecutable(final String pathToDirectory, final String gradlew) {
        final File f = new File(pathToDirectory + File.separator + gradlew);
        final boolean isExecutable = f.setExecutable(true);
    }

    public static void executeBuild(final File workingDir, final String[] command)
            throws InterruptedException, IOException {
        executeProcess(workingDir, command);
    }

    private static void executeProcess(File workingDir, String[] command)
            throws InterruptedException, IOException {
        int exitCode =
                new ProcessBuilder(command)
                        .directory(workingDir)
                        .redirectErrorStream(true)
                        .redirectOutput(SimpleFileLogger.INSTANCE.getLogFile())
                        .start()
                        .waitFor();
        if (exitCode != 0) {
            PrettyPrinter.INSTANCE.onFailed();
            System.exit(1);
        }
    }

    public static void createFatJar(String pathToDirectory)
            throws IOException, InterruptedException {
        if (!isWindows()) {
            executeProcess(
                    new File(pathToDirectory),
                    new String[] {"bash", "./gradlew", "shadowJar", "-q"});
        } else {
            executeProcess(
                    new File(pathToDirectory),
                    new String[] {"cmd", "/c", ".\\gradlew.bat shadowJar", "-q"});
        }
    }

    public static void generateTopLevelDirectories(ProjectStructure projectStructure) {
        projectStructure.createMainDirectory();
        projectStructure.createTestDirectory();
        projectStructure.createSolidityDirectory();
        projectStructure.createWrapperDirectory();
    }
}
