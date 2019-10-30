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
package org.web3j.console;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.web3j.utils.Numeric;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.crypto.Hash.sha256;

/** Simple class for creating a wallet file. */
public class WalletFunder {

    private static final String BASE_URL = "https://rinkeby.faucet.epirus.web3labs.com";
    private static final String USAGE = "fund <destination-address>";

    public static void main(IODevice console, String[] args) {
        if (args.length != 1 && args.length != 3) {
            exitError(USAGE);
        }

        try {
            String fund =
                    console.readLine(
                            "This command will fund the specified wallet on the Rinkeby testnet. Do you wish to continue? [Y/n]: ");
            if (!fund.toUpperCase().equals("Y")) {
                exitError("Operation was cancelled by user.");
            }
            String transactionHash = fundWallet(args[0], args.length == 3 ? args[2] : null);
            System.out.println(
                    String.format(
                            "Your Rinkeby wallet was successfully funded. You can view the associated transaction here, after it has been mined: https://rinkeby.explorer.epirus.web3labs.com/transactions/%s",
                            transactionHash));
            System.exit(0);
        } catch (Exception e) {
            System.err.println("The fund operation failed with the following exception:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean loading = true;

    private static synchronized void loading(String msg) {
        Thread th =
                new Thread(
                        () -> {
                            String anim = "|/―\\";
                            try {
                                System.out.write("\r|".getBytes());
                                int current = 0;
                                while (loading) {
                                    current++;
                                    String data =
                                            String.format(
                                                    "\r[ %s ] %s",
                                                    anim.charAt(current % anim.length()), msg);
                                    System.out.write(data.getBytes());
                                    Thread.sleep(500);
                                }
                                System.out.write("\n".getBytes());
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
        th.start();
    }

    private static String fundWallet(String walletAddress, String token) throws Exception {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Sending funding request...");
        Request sendEtherRequest;

        if (token != null) {
            RequestBody fundingBody =
                    new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("address", walletAddress)
                            .build();

            sendEtherRequest =
                    new okhttp3.Request.Builder()
                            .url(String.format("%s/send/%s", BASE_URL, token))
                            .post(fundingBody)
                            .build();
        } else {
            Request getSeedRequest =
                    new okhttp3.Request.Builder()
                            .url(String.format("%s/seed/0.2", BASE_URL))
                            .get()
                            .build();
            Response configRawResponse = client.newCall(getSeedRequest).execute();

            if (configRawResponse.code() != 200) {
                exitError("An HTTP request failed with code: " + configRawResponse.code());
            }

            String configResponse = configRawResponse.body().string();

            WalletFunderConfig config = mapper.readValue(configResponse, WalletFunderConfig.class);

            AtomicBoolean found = new AtomicBoolean(false);
            AtomicInteger intResult = new AtomicInteger(0);
            loading("Performing proof of work to validate your request");

            IntStream.range(0, Integer.MAX_VALUE)
                    .parallel()
                    .forEach(
                            i -> {
                                if (found.get()) return;
                                String potentialHash =
                                        Numeric.toHexString(
                                                        sha256(
                                                                (i + config.seed)
                                                                        .getBytes(
                                                                                StandardCharsets
                                                                                        .UTF_8)))
                                                .substring(2);
                                if (potentialHash.startsWith("0".repeat(config.difficulty))) {
                                    found.set(true);
                                    intResult.set(i);
                                }
                            });

            loading = false;
            RequestBody fundingBody =
                    new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("address", walletAddress)
                            .addFormDataPart("seed", config.seed)
                            .addFormDataPart("nonce", String.valueOf(intResult.get()))
                            .build();

            sendEtherRequest =
                    new okhttp3.Request.Builder()
                            .url(String.format("%s/send", BASE_URL))
                            .post(fundingBody)
                            .build();
        }

        Response sendRawResponse = client.newCall(sendEtherRequest).execute();

        if (sendRawResponse.code() != 200) {
            exitError(
                    String.format("An HTTP request failed with code: %d", sendRawResponse.code()));
        }

        String sendResponse = sendRawResponse.body().string();

        WalletFunderResult result = mapper.readValue(sendResponse, WalletFunderResult.class);

        return result.result;
    }
}

class WalletFunderConfig {
    public int difficulty;
    public String seed;
}

class WalletFunderResult {
    public String result;
}
