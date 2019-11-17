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
package org.web3j.console.update;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.web3j.console.config.CliConfig;
import org.web3j.utils.Version;

public class Updater {

    private CliConfig config;

    public Updater(CliConfig config) {
        this.config = config;
    }

    public void promptIfUpdateAvailable() {
        if (config.isUpdateAvailable()) {
            System.out.println(
                    String.format("A new Web3j update is available: %s", config.getUpdatePrompt()));
        }
    }

    public void onlineUpdateCheck() {
        OkHttpClient client = new OkHttpClient();

        RequestBody updateBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("os", CliConfig.determineOS().toString())
                        .addFormDataPart("clientId", config.getClientId())
                        .build();

        Request updateCheckRequest =
                new okhttp3.Request.Builder()
                        .url(
                                String.format(
                                        "%s/api/v1/versioning/versions/", config.getServicesUrl()))
                        .post(updateBody)
                        .build();

        try {
            Response sendRawResponse = client.newCall(updateCheckRequest).execute();
            if (sendRawResponse.code() == 200) {
                JsonObject rootObj =
                        JsonParser.parseString(sendRawResponse.body().string())
                                .getAsJsonObject()
                                .get("latest")
                                .getAsJsonObject();
                String currentVersion = rootObj.get("version").getAsString();
                if (!currentVersion.equals(Version.getVersion())) {
                    config.setUpdateAvailable(true);
                    config.setUpdatePrompt(
                            rootObj.get(
                                            CliConfig.determineOS() == CliConfig.OS.WINDOWS
                                                    ? "install_win"
                                                    : "install_unix")
                                    .getAsString());
                    config.save();
                }
            }
        } catch (IOException ignored) {
        }
    }
}
