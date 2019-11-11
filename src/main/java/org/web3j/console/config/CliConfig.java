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

    public static CliConfig initializeDefaultConfig(File configFile) throws IOException {
        if (!web3jHome.exists() && !web3jHome.mkdirs()) {
            throw new IOException("Failed to create Web3j home directory");
        }
        CliConfig defaultCliConfig =
                new CliConfig(Version.getVersion(), UUID.randomUUID().toString());
        String jsonToWrite = new Gson().toJson(defaultCliConfig);
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
        writer.write(jsonToWrite);
        writer.close();
        return defaultCliConfig;
    }

    public static CliConfig getSavedConfig(File configFile) throws IOException {
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

    private String version;
    private String clientId;

    public CliConfig(
            String version, String clientId, boolean updateAvailable, String updatePrompt) {
        this.version = version;
        this.clientId = clientId;
        this.updateAvailable = updateAvailable;
        this.updatePrompt = updatePrompt;
    }

    private boolean updateAvailable;
    private String updatePrompt;

    public CliConfig(String version, String clientId) {
        this.version = version;
        this.clientId = clientId;
    }

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
