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
import java.nio.file.Files;
import java.util.UUID;

import com.google.gson.Gson;

import org.web3j.utils.Version;

public class CliConfig {
    private static File web3jHome = new File(System.getProperty("user.home"), ".web3j");

    private static CliConfig initializeDefaultConfig(File configFile) throws IOException {
        if (!web3jHome.exists() && !web3jHome.mkdirs()) {
            throw new IOException("Failed to create Web3j home directory");
        }
        CliConfig defaultCliConfig = new CliConfig(UUID.randomUUID().toString(), false, null);
        String jsonToWrite = new Gson().toJson(defaultCliConfig);
        Files.writeString(configFile.toPath(), jsonToWrite);
        return defaultCliConfig;
    }

    private static CliConfig getSavedConfig(File configFile) throws IOException {
        String configContents = Files.readString(configFile.toPath());
        return new Gson().fromJson(configContents, CliConfig.class);
    }

    public static CliConfig getConfig() throws IOException {
        File web3jConfigFile = new File(web3jHome, ".config");
        if (web3jConfigFile.exists()) {
            return getSavedConfig(web3jConfigFile);
        } else {
            return initializeDefaultConfig(web3jConfigFile);
        }
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
        String osName = System.getProperty("os.name").split(" ")[0];
        if (osName.toLowerCase().startsWith("mac") || osName.toLowerCase().startsWith("darwin")) {
            return OS.DARWIN;
        } else if (osName.toLowerCase().startsWith("linux")) {
            return OS.LINUX;
        } else if (osName.toLowerCase().startsWith("sunos") || osName.toLowerCase().startsWith("solaris")) {
            return OS.SOLARIS;
        } else if (osName.toLowerCase().startsWith("aix")) {
            return OS.AIX;
        } else if (osName.toLowerCase().startsWith("openbsd")) {
            return OS.OPENBSD;
        } else if (osName.toLowerCase().startsWith("freebsd")) {
            return OS.FREEBSD;
        } else if (osName.toLowerCase().startsWith("windows")) {
            return OS.WINDOWS;
        } else {
            return OS.UNKNOWN;
        }
    }


    private transient final String version;
    private String clientId;

    private CliConfig() throws IOException {
        version = Version.getVersion();
    }

    public CliConfig(
            String clientId, boolean updateAvailable, String updatePrompt) throws IOException {
        version = Version.getVersion();
        this.clientId = clientId;
        this.updateAvailable = updateAvailable;
        this.updatePrompt = updatePrompt;
    }

    private boolean updateAvailable;
    private String updatePrompt;


    public String getVersion() {
        return version;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    public String getUpdatePrompt() {
        return updatePrompt;
    }

    public void setUpdatePrompt(String updatePrompt) {
        this.updatePrompt = updatePrompt;
    }
}
