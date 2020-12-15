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
package org.web3j.console.services;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.web3j.console.Web3jCommand;
import org.web3j.console.utils.OSUtils;

import static org.web3j.console.config.ConfigManager.config;

public class Telemetry {
    private static final String DEFAULT_TELEMETRY_URL =
            "https://internal.services.web3labs.com/api/analytics";

    public static void invokeTelemetryUpload(String... args)
            throws URISyntaxException, IOException {
        final String jarFile =
                new File(
                                Web3jCommand.class
                                        .getProtectionDomain()
                                        .getCodeSource()
                                        .getLocation()
                                        .toURI())
                        .getPath();
        if (jarFile.endsWith(".jar")) {
            Runtime.getRuntime()
                    .exec(
                            Stream.of(new String[] {"java", "-jar", jarFile, "--telemetry"}, args)
                                    .flatMap(Stream::of)
                                    .toArray(String[]::new));
        }
    }

    public static void uploadTelemetry(String... args) {
        uploadTelemetry(DEFAULT_TELEMETRY_URL, args);
    }

    public static void uploadTelemetry(String telemetryUrl, String[] args) {
        OkHttpClient client = new OkHttpClient();
        String argsToUpload = Stream.of(args).skip(2).collect(Collectors.joining(", "));

        RequestBody analyticsBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("os", OSUtils.determineOS().toString())
                        .addFormDataPart("clientId", config.getClientId())
                        .addFormDataPart("data", args.length >= 2 ? args[1] : "No args")
                        .addFormDataPart("params", argsToUpload)
                        .build();

        Request analyticsRequest =
                new Request.Builder().url(telemetryUrl).post(analyticsBody).build();

        try {
            client.newCall(analyticsRequest).execute();
        } catch (Exception ignored) {
        }
    }
}
