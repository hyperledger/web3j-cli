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
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

import org.web3j.console.project.utills.InputVerifier;

import static java.io.File.separator;
import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.project.utills.ProjectUtils.deleteFolder;

class InteractiveOptions {
    static Scanner scanner = new Scanner(System.in);

    static String getProjectName() {
        print("Please enter the project name (Required Field):");
        String projectName = getUserInput();
        while (!InputVerifier.classNameIsValid(projectName)) {
            projectName = getUserInput();
        }
        return projectName;
    }

    static String getPackageName() {
        print("Please enter the package name for your project (Required Field): ");
        String packageName = getUserInput();
        while (!InputVerifier.packageNameIsValid(packageName)) {
            packageName = getUserInput();
        }
        return packageName;
    }

    static Optional<String> getProjectDestination(final String projectName) {
        print("Please enter the destination of your project (Current by default): ");
        final String projectDest = getUserInput();
        final String projectPath = projectDest + separator + projectName;
        if (new File(projectPath).exists()) {
            if (overrideExistingProject()) {
                Path path = new File(projectPath).toPath();
                deleteFolder(path);
                return Optional.of(projectDest);
            } else {
                exitError("Project creation was canceled.");
            }
        }
        return projectDest.isEmpty() ? Optional.empty() : Optional.of(projectDest);
    }

    static Optional<String> getGeneratedWrapperLocation() {
        print("Please enter the path of the generated contract wrappers.");
        String pathToTheWrappers = getUserInput();
        return pathToTheWrappers.isEmpty() ? Optional.empty() : Optional.of(pathToTheWrappers);
    }

    static Optional<String> setGeneratedTestLocation() {
        print("Where would you like to save your tests.");
        String outputPath = getUserInput();
        return outputPath.isEmpty() ? Optional.empty() : Optional.of(outputPath);
    }

    static boolean userWantsTests() {
        print("Would you like to generate unit test for your solidity contracts [Y/n] ? ");
        String userAnswer = getUserInput();
        return userAnswer.trim().toLowerCase().equals("y") || userAnswer.trim().equals("");
    }

    static String getSolidityProjectPath() {
        System.out.println("Please enter the path to your solidity file/folder (Required Field): ");
        return getUserInput();
    }

    static String getUserInput() {

        return scanner.nextLine();
    }

    private static void print(final String text) {
        System.out.println(text);
    }


    static boolean overrideExistingProject() {
        print("Looks like the project exists. Would you like to override it [y/N] ?");
        String userAnswer = getUserInput();
        return userAnswer.toLowerCase().equals("y");
    }
}
