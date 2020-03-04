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
package org.web3j.console.account;

import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.web3j.console.config.CliConfig;

import static org.web3j.codegen.Console.exitError;

public class AccountManager {
    private static final String USAGE = "account login|logout|create";
    private static final String CLOUD_URL = "https://auth.epirus.io";
    private OkHttpClient client;
    CliConfig config;

    public AccountManager(final CliConfig cliConfig, OkHttpClient client) {
        this.client = client;
        this.config = cliConfig;
    }


    public static void main(final CliConfig config, final String[] args) {

        Scanner console = new Scanner(System.in);
        if ("create".equals(args[0])) {
            System.out.println("Please enter your email address: ");
            String email = console.nextLine().trim();
            new AccountManager(config, new OkHttpClient()).createAccount(email);

        } else {
            exitError(USAGE);
        }
    }

    public void createAccount(String email) {
        RequestBody requestBody = createRequestBody(email);
        Request newAccountRequest = createRequest(requestBody);

        try {
            Response sendRawResponse = executeClientCall(newAccountRequest);
            ResponseBody body;
            if (sendRawResponse.code() == 200
                    && (body = sendRawResponse.body()) != null) {
                String rawResponse = body.string();
                JsonObject responseJsonObj =
                        JsonParser.parseString(rawResponse).getAsJsonObject();

                if (responseJsonObj.get("token") == null) {
                    String tokenError = responseJsonObj.get("tokenError").getAsString();
                    System.out.println(tokenError);
                    return;
                }
                String token = responseJsonObj.get("token").getAsString();
                config.setLoginToken(token);
                System.out.println(
                        "Account created successfully. You can now use Web3j Cloud. Please confirm your e-mail within 24 hours to continue using all features without interruption.");
            } else {
                System.out.println("Account creation failed. Please try again later.");
            }


        } catch (IOException e) {
            System.out.println("Could not connect to the server.\nReason:" + e.getMessage());
        }
        client.connectionPool().evictAll();
    }

    protected final Response executeClientCall(Request newAccountRequest) throws IOException {
        return client.newCall(newAccountRequest).execute();
    }

    protected final RequestBody createRequestBody(String email) {

        return new FormBody.Builder().add("email", email)
                .build();


    }

    protected final Request createRequest(RequestBody accountBody) {

        return new Request.Builder()
                .url(
                        String.format(
                                "%s/auth/realms/EpirusPortal/web3j-token/create",
                                CLOUD_URL))
                .post(accountBody)
                .build();
    }
}
