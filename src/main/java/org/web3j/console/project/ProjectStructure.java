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

public abstract class ProjectStructure {

    public final String packageName;
    public final String projectName;
    protected final String rootDirectory;
    protected final String projectRoot;
    private final String pathToTestDirectory;
    private final String solidityPath;
    private final String mainPath;
    private final String wrapperPath;
    private final String walletPath;
    private final String generatedJavaWrapper;
    private final String testPath;

    protected ProjectStructure(
            final String rootDirectory,
            final String packageName,
            final String projectName,
            String projectType) {
        this.rootDirectory = generateRoot(rootDirectory);
        final String formattedPackageName = formatPackageName(packageName);
        this.packageName = packageName;
        this.projectName = projectName;
        this.projectRoot = this.rootDirectory + File.separator + projectName;
        this.mainPath =
                generatePath(this.projectRoot, "src", "main", projectType, formattedPackageName);
        this.solidityPath = generatePath(this.projectRoot, "src", "main", "solidity");
        this.pathToTestDirectory = generatePath(this.projectRoot, "src", "test", projectType);
        this.testPath =
                generatePath(this.projectRoot, "src", "test", projectType, formattedPackageName);
        this.walletPath = generatePath(this.projectRoot, "src", "test", "resources", "wallet");
        this.wrapperPath = generatePath(this.projectRoot, "gradle", "wrapper");
        this.generatedJavaWrapper =
                generatePath(
                        this.rootDirectory,
                        projectName,
                        "build",
                        "generated",
                        "sources",
                        "web3j",
                        "main",
                        "java");
    }

    protected String generateRoot(final String path) {
        if (path.equals("~")) {
            return System.getProperty("user.home");
        } else if (path.startsWith("~" + File.separator)) {
            return System.getProperty("user.home") + path.substring(1);
        } else if (path.equals(".")) {
            return System.getProperty("user.dir");
        } else if (path.startsWith(".")) {
            return System.getProperty("user.dir") + path.substring(1);
        }
        return path;
    }

    protected String generatePath(final String... a) {
        final StringBuilder finalPath = new StringBuilder();
        for (final String b : a) {
            finalPath.append(b).append(File.separator);
        }
        return finalPath.toString();
    }

    protected String formatPackageName(final String packageName) {
        if (packageName.contains(".")) {
            return packageName.replace(".", File.separator);
        }
        return packageName;
    }

    protected void createDirectory(final String path) {
        final File directory = new File(path);
        directory.mkdirs();
    }

    public void createMainDirectory() {
        createDirectory(mainPath);
    }

    public void createTestDirectory() {
        createDirectory(pathToTestDirectory);
    }

    public void createSolidityDirectory() {
        createDirectory(solidityPath);
    }

    public void createWrapperDirectory() {
        createDirectory(wrapperPath);
    }

    public void createWalletDirectory() {
        createDirectory(walletPath);
    }

    public final String getPackageName() {
        return packageName;
    }

    public final String getProjectName() {
        return projectName;
    }

    public final String getPathToTestDirectory() {
        return pathToTestDirectory;
    }

    public final String getTestPath() {
        return testPath;
    }

    public final String getGeneratedJavaWrappers() {
        return generatedJavaWrapper;
    }

    public final String getSolidityPath() {
        return solidityPath;
    }

    public final String getWalletPath() {
        return walletPath;
    }

    public final String getMainPath() {
        return mainPath;
    }

    public final String getWrapperPath() {
        return wrapperPath;
    }

    public final String getRootDirectory() {
        return rootDirectory;
    }

    public final String getProjectRoot() {
        return projectRoot;
    }
}
