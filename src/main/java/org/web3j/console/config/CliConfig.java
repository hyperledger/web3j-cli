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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class CliConfig {
    private String clientId;
    private String latestVersion;
    private String updatePrompt;
    private String defaultWalletPath;
    private String defaultWalletPassword;
    private boolean telemetryDisabled;

    @Expose(serialize = false, deserialize = false)
    protected transient boolean isPersistent = false;

    protected CliConfig(
            String clientId,
            String latestVersion,
            String updatePrompt,
            String defaultWalletPath,
            String defaultWalletPassword,
            boolean telemetryDisabled) {
        this.clientId = clientId;
        this.latestVersion = latestVersion;
        this.updatePrompt = updatePrompt;
        this.defaultWalletPath = defaultWalletPath;
        this.defaultWalletPassword = defaultWalletPassword;
        this.telemetryDisabled = telemetryDisabled;
    }

    public String getClientId() {
        return clientId;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getUpdatePrompt() {
        return updatePrompt;
    }

    public String getDefaultWalletPath() {
        return defaultWalletPath;
    }

    public boolean isTelemetryDisabled() {
        return telemetryDisabled;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
        save();
    }

    public void setUpdatePrompt(String updatePrompt) {
        this.updatePrompt = updatePrompt;
        save();
    }

    public void setDefaultWalletPath(final String defaultWalletPath) {
        this.defaultWalletPath = defaultWalletPath;
        save();
    }

    public void setDefaultWalletPassword(final String defaultWalletPassword) {
        this.defaultWalletPassword = defaultWalletPassword;
        save();
    }

    public void save() {
        if (!isPersistent) return;

        String jsonToWrite = new Gson().toJson(this);
        try {
            Files.write(
                    ConfigManager.DEFAULT_WEB3J_CONFIG_PATH,
                    jsonToWrite.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public void setPersistent(boolean persistent) {
        isPersistent = persistent;
    }

    public String getDefaultWalletPassword() {
        return defaultWalletPassword;
    }
}
