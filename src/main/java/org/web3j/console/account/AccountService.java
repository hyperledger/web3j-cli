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

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;

import com.diogonunes.jcdp.color.api.Ansi;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.epirus.web3j.Epirus;
import okhttp3.*;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Network;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.console.config.ConfigManager.config;
import static org.web3j.console.utils.PrinterUtilities.printErrorAndExit;
import static org.web3j.console.utils.PrinterUtilities.printInformationPairWithStatus;

public class AccountService implements Closeable {

    public static final String DEFAULT_APP_URL =
            System.getenv().getOrDefault("EPIRUS_APP_URL", "https://portal.epirus.io");

    private final String cloudURL;
    private final OkHttpClient client = new OkHttpClient();

    @VisibleForTesting
    public AccountService(String cloudURL) {
        this.cloudURL = cloudURL;
    }

    public AccountService() {
        this(DEFAULT_APP_URL);
    }

    public boolean createAccount(String email) {
        String accountResponse =
                accountRequest(
                        "/api/users/create/", new FormBody.Builder().add("email", email).build());

        JsonObject responseJsonObj = JsonParser.parseString(accountResponse).getAsJsonObject();

        if (responseJsonObj.get("token") == null) {
            return false;
        }
        String token = responseJsonObj.get("token").getAsString();
        config.setLoginToken(token);
        return true;
    }

    public boolean authenticate(String email, String password) {
        String authenticateResponse =
                accountRequest(
                        "/api/users/authenticate/",
                        new FormBody.Builder()
                                .add("email", email)
                                .add("password", password)
                                .build());
        JsonObject responseJsonObj = JsonParser.parseString(authenticateResponse).getAsJsonObject();
        if (responseJsonObj.get("token") == null) {
            return false;
        }
        String token = responseJsonObj.get("token").getAsString();
        config.setLoginToken(token);
        return true;
    }

    public String accountRequest(String url, FormBody body) {
        Request accountRequest =
                new Request.Builder().url(String.format("%s%s", cloudURL, url)).post(body).build();

        try {
            Response sendRawResponse = client.newCall(accountRequest).execute();
            ResponseBody responseBody;
            if (sendRawResponse.code() == 200 && (responseBody = sendRawResponse.body()) != null) {
                return responseBody.string();
            } else if (sendRawResponse.code() == 401) {
                printErrorAndExit(
                        "Your login attempt failed. Please check your username & password are correct.");
            } else {
                printErrorAndExit("Your login attempt failed. Please try again later.");
            }
        } catch (IOException e) {
            printErrorAndExit(
                    "Could not connect to the Epirus Cloud server.\nReason:" + e.getMessage());
        }
        throw new RuntimeException();
    }

    public boolean checkIfAccountIsConfirmed(int tries) throws IOException, InterruptedException {
        Request request =
                new Request.Builder()
                        .url(
                                String.format(
                                        "%s/api/users/status/%s", cloudURL, config.getLoginToken()))
                        .get()
                        .build();
        while (tries-- > 0) {
            if (userConfirmedAccount(request)) {
                return true;
            } else {
                printInformationPairWithStatus(
                        "Account status", 20, "PENDING ", Ansi.FColor.YELLOW);
            }
            Thread.sleep(10000);
        }
        return false;
    }

    private boolean userConfirmedAccount(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();

        if (response.code() == 401) {
            exitError("Your current login token is invalid. Please log out & log in again.");
        }

        if (response.code() != 200 || responseBody == null) {
            return false;
        }
        JsonObject responseJsonObj =
                JsonParser.parseString(responseBody.string()).getAsJsonObject();
        return responseJsonObj.get("active").getAsBoolean();
    }

    public BigInteger getAccountBalance(Credentials credentials, Web3j web3j) {
        int count = 0;
        int maxTries = 10;
        while (true) {
            try {
                EthGetBalance accountBalance =
                        web3j.ethGetBalance(
                                        credentials.getAddress(), DefaultBlockParameterName.LATEST)
                                .send();
                if (accountBalance.getError() == null) {
                    return accountBalance.getBalance();
                }

            } catch (Exception e) {
                if (++count == maxTries) {
                    printErrorAndExit(e.getMessage());
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public BigInteger pollForAccountBalance(
            Credentials credentials, Network network, Web3j web3j, int numberOfBlocksToCheck)
            throws IOException {
        BigInteger accountBalance = null;
        BigInteger startBlock = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger stopBlock = startBlock.add(BigInteger.valueOf(numberOfBlocksToCheck));
        while (web3j.ethBlockNumber().send().getBlockNumber().compareTo(stopBlock) < 0) {
            try {
                accountBalance =
                        Epirus.buildWeb3j(Network.valueOf(network.getNetworkName().toUpperCase()))
                                .ethGetBalance(
                                        credentials.getAddress(), DefaultBlockParameterName.LATEST)
                                .send()
                                .getBalance();
                if (accountBalance.compareTo(BigInteger.ZERO) > 0) {
                    return accountBalance;
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                printErrorAndExit("Could not check the account balance." + e.getMessage());
            }
        }
        return accountBalance;
    }

    public String getLoginToken() {
        return config.getLoginToken();
    }

    @Override
    public void close() {
        this.client.dispatcher().executorService().shutdown();
        this.client.connectionPool().evictAll();
    }
}
