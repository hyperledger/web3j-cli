/*
 * Copyright 2019 Web3 Labs LTD.
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
package org.web3j.console.config;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.Gson;

import org.web3j.utils.Version;

public class CliConfig {
    private static final Path web3jConfigPath =
            Paths.get(System.getProperty("user.home"), ".web3j", ".config");
    private static final String defaultServicesUrl = "http://localhost:8000";

    public static Path getWeb3jConfigPath() {
        return web3jConfigPath;
    }

    private static CliConfig initializeDefaultConfig(File configFile) throws IOException {
        File web3jHome = new File(configFile.getParent());
        if (!web3jHome.exists() && !web3jHome.mkdirs()) {
            throw new IOException("Failed to create Web3j home directory");
        }
        return new CliConfig(
                Version.getVersion(),
                defaultServicesUrl,
                UUID.randomUUID().toString(),
                Version.getVersion(),
                null);
    }

    private static CliConfig getSavedConfig(File configFile) throws IOException {
        String configContents = new String(Files.readAllBytes(configFile.toPath()));
        return new Gson().fromJson(configContents, CliConfig.class);
    }

    public static CliConfig getConfig(File configFile) throws IOException {
        if (configFile.exists()) {
            return getSavedConfig(configFile);
        } else {
            return initializeDefaultConfig(configFile);
        }
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isUpdateAvailable() {
        return !version.equals(latestVersion);
    }

    public enum OS {
        DARWIN,
        FREEBSD,
        OPENBSD,
        LINUX,
        SOLARIS,
        WINDOWS,
        AIX,
        UNKNOWN;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static OS determineOS() {
        String osName = System.getProperty("os.name").split(" ")[0].toLowerCase();
        if (osName.startsWith("mac") || osName.startsWith("darwin")) {
            return OS.DARWIN;
        } else if (osName.startsWith("linux")) {
            return OS.LINUX;
        } else if (osName.startsWith("sunos") || osName.startsWith("solaris")) {
            return OS.SOLARIS;
        } else if (osName.startsWith("aix")) {
            return OS.AIX;
        } else if (osName.startsWith("openbsd")) {
            return OS.OPENBSD;
        } else if (osName.startsWith("freebsd")) {
            return OS.FREEBSD;
        } else if (osName.startsWith("windows")) {
            return OS.WINDOWS;
        } else {
            return OS.UNKNOWN;
        }
    }

    private String version;
    private String servicesUrl;
    private String clientId;
    private String latestVersion;
    private String updatePrompt;

    public CliConfig(
            String version,
            String servicesUrl,
            String clientId,
            String latestVersion,
            String updatePrompt) {
        this.version = version;
        this.servicesUrl = servicesUrl;
        this.clientId = clientId;
        this.latestVersion = latestVersion;
        this.updatePrompt = updatePrompt;
    }



    public String getVersion() {
        return version;
    }

    public String getServicesUrl() {
        return servicesUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUpdatePrompt() {
        return updatePrompt;
    }

    public void setUpdatePrompt(String updatePrompt) {
        this.updatePrompt = updatePrompt;
    }

    public void save() throws IOException {
        String jsonToWrite = new Gson().toJson(this);
        Files.write(web3jConfigPath, jsonToWrite.getBytes(Charset.defaultCharset()));
    }
}
