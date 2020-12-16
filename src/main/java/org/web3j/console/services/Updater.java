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
package org.web3j.console.services;

import java.io.IOException;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import org.web3j.console.utils.CliVersion;
import org.web3j.console.utils.OSUtils;

import static org.web3j.console.config.ConfigManager.config;

public class Updater {
    private static final String DEFAULT_UPDATE_URL =
            "https://internal.services.web3labs.com/api/epirus/versions/latest";

    public static void promptIfUpdateAvailable() throws IOException {
        String version = CliVersion.getVersion();
        if (config.getLatestVersion() != null
                && Version.valueOf(config.getLatestVersion()).greaterThan(Version.valueOf(version))
                && !version.contains("SNAPSHOT")) {
            System.out.println(
                    String.format(
                            "Your current Web3j version is: "
                                    + version
                                    + ". The latest Version is: "
                                    + config.getLatestVersion()
                                    + ". To update, run: %s",
                            config.getUpdatePrompt()));
        }
    }

    public static void onlineUpdateCheck() {
        onlineUpdateCheck(DEFAULT_UPDATE_URL);
    }

    public static void onlineUpdateCheck(String updateUrl) {
        OkHttpClient client = new OkHttpClient();

        RequestBody updateBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("os", OSUtils.determineOS().toString())
                        .addFormDataPart("clientId", config.getClientId())
                        .addFormDataPart("data", "update_check")
                        .build();

        Request updateCheckRequest = new Request.Builder().url(updateUrl).post(updateBody).build();

        try {
            Response sendRawResponse = client.newCall(updateCheckRequest).execute();
            JsonElement element;
            ResponseBody body;
            if (sendRawResponse.code() == 200
                    && (body = sendRawResponse.body()) != null
                    && (element = JsonParser.parseString(body.string())) != null
                    && element.isJsonObject()) {
                JsonObject rootObj = element.getAsJsonObject().get("latest").getAsJsonObject();
                String latestVersion = rootObj.get("version").getAsString();
                if (!latestVersion.equals(CliVersion.getVersion())) {
                    config.setLatestVersion(latestVersion);
                    config.setUpdatePrompt(
                            rootObj.get(
                                            OSUtils.determineOS() == OSUtils.OS.WINDOWS
                                                    ? "install_win"
                                                    : "install_unix")
                                    .getAsString());
                }
            }
        } catch (Exception ignored) {
        }
    }
}
