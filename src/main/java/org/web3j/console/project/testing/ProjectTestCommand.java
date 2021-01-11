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
package org.web3j.console.project.testing;

import java.io.File;

import picocli.CommandLine.Command;

import org.web3j.codegen.Console;
import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.utils.OSUtils;

@Command(
        name = "test",
        description = "Run the tests of a gradle project",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class ProjectTestCommand implements Runnable {
    private static boolean setExecutable(final String pathToDirectory, final String gradlew) {
        final File f = new File(pathToDirectory + File.separator + gradlew);
        return f.setExecutable(true);
    }

    public void run() {
        String currentDirPath = System.getProperty("user.dir");

        if (OSUtils.determineOS() == OSUtils.OS.WINDOWS) {
            setExecutable(currentDirPath, "gradlew.bat");
            runTests(new File(currentDirPath), new String[] {"cmd", "/c", ".\\gradlew.bat test"});
        } else if (OSUtils.determineOS() == OSUtils.OS.LINUX) {
            setExecutable(currentDirPath, "gradlew");
            runTests(new File(currentDirPath), new String[] {"bash", "-c", "./gradlew test"});
        }
    }

    private static void runTests(File workingDir, String[] command) {
        int exitCode = 0;
        try {
            exitCode =
                    new ProcessBuilder(command)
                            .directory(workingDir)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                            .start()
                            .waitFor();
        } catch (Exception e) {
            Console.exitError(e);
        }
        if (exitCode != 0) {
            Console.exitError("Tests failed. For more details, see the test output.");
        } else {
            System.out.println("Web3j successfully tested your application.");
        }
    }
}
