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
package org.web3j.console.wallet.subcommands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.wallet.Faucet;
import org.web3j.console.wallet.WalletManager;
import org.web3j.utils.Numeric;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.crypto.Hash.sha256;

@Command(
        name = "fund",
        description = "Fund testnet wallets.",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class WalletFundCommand extends WalletManager implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "network",
            description = "Ethereum network [rinkeby/kovan]",
            arity = "1")
    String network;

    @Parameters(
            index = "1",
            paramLabel = "destination-address",
            description = "Ethereum 20 bytes hex address",
            arity = "1")
    String destinationAddress;

    @Option(names = {"-t", "--token"})
    protected String token;

    @Override
    public void run() {
        try {
            Faucet selectedFaucet = Faucet.valueOf(network.toUpperCase());

            String fund =
                    request(
                            "This command will fund the specified wallet on the %s testnet. Do you wish to continue? [Y/n]: ",
                            selectedFaucet.name);
            if (fund.toUpperCase().equals("N")) {
                exitError("Operation was cancelled by user.");
            }
            setTokenIfAvailable();
            String transactionHash = fundWallet(destinationAddress, selectedFaucet, token);
            System.out.printf(
                    "Your wallet was successfully funded. You can view the associated transaction here, after it has been mined: https://%s.epirus.io/transactions/%s%n",
                    selectedFaucet.name.toLowerCase(), transactionHash);
        } catch (Exception e) {
            System.err.println("The fund operation failed");
            System.exit(-1);
        }
    }

    private static boolean loading = true;

    private static synchronized void loading() {
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
                                                    anim.charAt(current % anim.length()),
                                                    "Performing proof of work to validate your request");
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

    public static String fundWallet(String walletAddress, Faucet faucet, String token)
            throws Exception {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

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
                    new Request.Builder()
                            .url(String.format("%s/send/%s", faucet.url, token))
                            .post(fundingBody)
                            .build();
        } else {
            Request getSeedRequest =
                    new Request.Builder()
                            .url(String.format("%s/seed/0.2", faucet.url))
                            .get()
                            .build();
            Response configRawResponse = client.newCall(getSeedRequest).execute();

            if (configRawResponse.code() != 200) {
                exitError("An HTTP request failed with code: " + configRawResponse.code());
            }

            String configResponse = configRawResponse.body().string();

            WalletFundConfig config = mapper.readValue(configResponse, WalletFundConfig.class);

            AtomicBoolean found = new AtomicBoolean(false);
            AtomicInteger intResult = new AtomicInteger(0);
            loading();

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
                                if (potentialHash.startsWith(
                                        new String(new char[config.difficulty])
                                                .replace("\0", "0"))) {
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
                    new Request.Builder()
                            .url(String.format("%s/send", faucet.url))
                            .post(fundingBody)
                            .build();
        }
        try {
            Response sendRawResponse = client.newCall(sendEtherRequest).execute();

            if (sendRawResponse.code() != 200) {
                exitError(
                        String.format(
                                "\nAn HTTP request failed with code: %d", sendRawResponse.code()));
            }

            String sendResponse = sendRawResponse.body().string();

            WalletFundResult result = mapper.readValue(sendResponse, WalletFundResult.class);
            return result.result;
        } catch (Exception ex) {
            throw new Exception(
                    "The fund operation failed - this may be due to an issue with the remote server. Please try again.",
                    ex);
        }
    }

    public String setTokenIfAvailable() {
        return token == null ? null : token;
    }
}

class WalletFundConfig {
    public int difficulty;
    public String seed;
}

class WalletFundResult {
    public String result;
}
