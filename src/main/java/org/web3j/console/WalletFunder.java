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

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.crypto.Hash.sha256;

/**
 * Simple class for creating a wallet file.
 */
public class WalletFunder {

    private static final String BASE_URL = "http://localhost:8000";
    private static final String USAGE = "fund <destination-address>";

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            exitError(USAGE);
        }

        try {
            String transactionHash = fundWallet(args[0]);
            System.out.println(
                    String.format(
                            "Your Rinkeby wallet was successfully funded. You can view the associated transaction here: https://rinkeby.explorer.epirus.web3labs.com/transactions/%s",
                            transactionHash));
        } catch (Exception e) {
            System.err.println("The fund operation failed with the following exception:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean loading = true;

    private static synchronized void loading(String msg) {
        Thread th = new Thread(() -> {
            String anim= "|/―\\";
            try {
                System.out.write("\r|".getBytes());
                int current = 0;
                while(loading) {
                    current++;
                    String data = "\r" + "[ " + anim.charAt(current % anim.length()) + " ] " + msg;
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

    private static String fundWallet(String walletAddress) throws Exception {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Sending funding request...");
        Request getSeedRequest =
                new okhttp3.Request.Builder().url(BASE_URL + "/seed/0.2").get().build();

        String configResponse = client.newCall(getSeedRequest).execute().body().string();

        WalletFunderConfig config = mapper.readValue(configResponse, WalletFunderConfig.class);

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger intResult = new AtomicInteger(0);
        loading("Performing proof of work to validate your request");

        IntStream.range(0, Integer.MAX_VALUE).parallel().forEach(i -> {
            if (found.get()) return;
            String potentialHash = Numeric.toHexString(sha256((i + config.seed).getBytes(StandardCharsets.UTF_8))).substring(2);
            if (potentialHash.startsWith("0".repeat(config.difficulty))) {
                found.set(true);
                intResult.set(i);
            }
        });

        loading = false;
        RequestBody fundingBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("address", walletAddress)
                .addFormDataPart("seed", config.seed)
                .addFormDataPart("nonce", String.valueOf(intResult.get()))
                .build();

        Request sendEtherRequest = new okhttp3.Request.Builder().url(BASE_URL + "/send").post(fundingBody).build();

        String sendResponse = client.newCall(sendEtherRequest).execute().body().string();

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