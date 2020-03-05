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
package org.web3j.console.telemetry;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.web3j.console.Runner;
import org.web3j.console.config.CliConfig;

public class Telemetry {

    public static void invokeAnalyticsUpload(String... args)
            throws URISyntaxException, IOException {
        final String jarFile =
                new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                        .getPath();
        if (jarFile.endsWith(".jar")) {
            Runtime.getRuntime()
                    .exec(
                            Stream.concat(
                                            Arrays.stream(
                                                    new String[] {
                                                        "java", "-jar", jarFile, "--telemetry"
                                                    }),
                                            Arrays.stream(args))
                                    .toArray(String[]::new));
        }
    }

    public static void uploadAnalytics(CliConfig config, String... args) {
        OkHttpClient client = new OkHttpClient();
        ArrayList<String> allArgs =
                Arrays.stream(args).skip(1).collect(Collectors.toCollection(ArrayList::new));
        String argsToUpload = allArgs.stream().skip(1).collect(Collectors.joining(", "));

        RequestBody analyticsBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("os", CliConfig.determineOS().toString())
                        .addFormDataPart("clientId", config.getClientId())
                        .addFormDataPart("data", allArgs.get(0))
                        .addFormDataPart("params", argsToUpload)
                        .build();

        Request analyticsRequest =
                new okhttp3.Request.Builder()
                        .url(String.format("%s/api/analytics", config.getServicesUrl()))
                        .post(analyticsBody)
                        .build();

        try {
            client.newCall(analyticsRequest).execute();
        } catch (Exception ignored) {
        }
    }
}
