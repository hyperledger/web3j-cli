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
package org.web3j.console.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;

public class ConfigManager {
    protected static final Path DEFAULT_WEB3J_CONFIG_PATH =
            Paths.get(System.getProperty("user.home"), ".web3j", ".config");

    public static CliConfig config;

    public static void setProduction() throws IOException {
        CliConfig productionConfig = getDefaultConfig(DEFAULT_WEB3J_CONFIG_PATH.toFile());
        productionConfig.setPersistent(true);
        config = productionConfig;
        if (!DEFAULT_WEB3J_CONFIG_PATH.toFile().exists()) {
            config.save();
        }
    }

    @VisibleForTesting
    public static void setDevelopment() throws IOException {
        config = getDefaultConfig(DEFAULT_WEB3J_CONFIG_PATH.toFile());
    }

    @VisibleForTesting
    public static void setDevelopment(
            String clientId,
            String latestVersion,
            String updatePrompt,
            String defaultWalletPath,
            String defaultWalletPassword,
            boolean telemetryDisabled) {
        config =
                new CliConfig(
                        clientId,
                        latestVersion,
                        updatePrompt,
                        defaultWalletPath,
                        defaultWalletPassword,
                        telemetryDisabled);
    }

    private static CliConfig initializeDefaultConfig(File configFile) throws IOException {
        File web3jHome = new File(configFile.getParent());
        if (!web3jHome.exists() && !web3jHome.mkdirs()) {
            throw new IOException("Failed to create Web3j home directory");
        }
        return new CliConfig(UUID.randomUUID().toString(), null, null, null, null, false);
    }

    private static CliConfig getSavedConfig(File configFile) throws IOException {
        String configContents = new String(Files.readAllBytes(configFile.toPath()));
        return new Gson().fromJson(configContents, CliConfig.class);
    }

    private static CliConfig getDefaultConfig(File configFile) throws IOException {
        if (configFile.exists()) {
            return getSavedConfig(configFile);
        } else {
            return initializeDefaultConfig(configFile);
        }
    }
}
