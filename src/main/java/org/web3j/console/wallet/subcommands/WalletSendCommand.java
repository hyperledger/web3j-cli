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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.wallet.WalletManager;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.protocol.nodesmith.NodesmithHttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import static org.web3j.codegen.Console.exitError;

@Command(
        name = "send",
        description = "Send to Ethereum wallet",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class WalletSendCommand extends WalletManager implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "wallet-path",
            description = "Path/filename of the wallet file",
            arity = "1")
    String walletFileLocation;

    @Parameters(
            index = "1",
            paramLabel = "destination-address",
            description = "Ethereum 20 bytes hex address",
            arity = "1")
    String destinationAddress;

    @Override
    public void run() {
        File walletFile = new File(walletFileLocation);
        Credentials credentials = getCredentials(walletFile);
        notify("Wallet for address " + credentials.getAddress() + " loaded\n");

        if (!WalletUtils.isValidAddress(destinationAddress)
                && !EnsResolver.isValidEnsName(destinationAddress)) {
            exitError("Invalid destination address specified");
        }

        org.web3j.protocol.Web3j web3j = getEthereumClient();

        BigDecimal amountToTransfer = getAmountToTransfer();
        Convert.Unit transferUnit = getTransferUnit();
        BigDecimal amountInWei = Convert.toWei(amountToTransfer, transferUnit);

        confirmTransfer(amountToTransfer, transferUnit, amountInWei, destinationAddress);

        TransactionReceipt transactionReceipt =
                performTransfer(web3j, destinationAddress, credentials, amountInWei);

        notify(
                "Funds have been successfully transferred from %s to %s%n"
                        + "Transaction hash: %s%nMined block number: %s%n",
                credentials.getAddress(),
                destinationAddress,
                transactionReceipt.getTransactionHash(),
                transactionReceipt.getBlockNumber());
    }

    private BigDecimal getAmountToTransfer() {
        String amount =
                request(
                                "What amount would you like to transfer "
                                        + "(please enter a numeric value): ")
                        .trim();
        try {
            return new BigDecimal(amount);
        } catch (NumberFormatException e) {
            exitError("Invalid amount specified");
        }
        throw new RuntimeException("Application exit failure");
    }

    private Convert.Unit getTransferUnit() {
        String unit = request("Please specify the unit (ether, wei, ...) [ether]: ");

        Convert.Unit transferUnit;
        if (unit.equals("")) {
            transferUnit = Convert.Unit.ETHER;
        } else {
            transferUnit = Convert.Unit.fromString(unit.toLowerCase());
        }

        return transferUnit;
    }

    private void confirmTransfer(
            BigDecimal amountToTransfer,
            Convert.Unit transferUnit,
            BigDecimal amountInWei,
            String destinationAddress) {

        request(
                "Please confim that you wish to transfer %s %s (%s %s) to address %s%n",
                amountToTransfer.stripTrailingZeros().toPlainString(),
                transferUnit,
                amountInWei.stripTrailingZeros().toPlainString(),
                Convert.Unit.WEI,
                destinationAddress);
        String confirm = request("Please type 'yes' to proceed: ");
        if (!confirm.toLowerCase().equals("yes")) {
            exitError("OK, some other time perhaps...");
        }
    }

    private TransactionReceipt performTransfer(
            org.web3j.protocol.Web3j web3j,
            String destinationAddress,
            Credentials credentials,
            BigDecimal amountInWei) {

        notify("Commencing transfer (this may take a few minutes) ");
        try {
            Future<TransactionReceipt> future =
                    Transfer.sendFunds(
                                    web3j,
                                    credentials,
                                    destinationAddress,
                                    amountInWei,
                                    Convert.Unit.WEI)
                            .sendAsync();

            while (!future.isDone()) {
                notify(".");
                Thread.sleep(500);
            }
            notify("$%n%n");
            return future.get();
        } catch (InterruptedException | ExecutionException | TransactionException | IOException e) {
            exitError("Problem encountered transferring funds: \n" + e.getMessage());
        }
        throw new RuntimeException("Application exit failure");
    }

    private org.web3j.protocol.Web3j getEthereumClient() {
        String clientAddress =
                request(
                                "Please confirm address of running Ethereum client you wish to send "
                                        + "the transfer request to ["
                                        + HttpService.DEFAULT_URL
                                        + "]: ")
                        .trim();

        org.web3j.protocol.Web3j web3j;
        if (clientAddress.equals("")) {
            web3j = org.web3j.protocol.Web3j.build(new HttpService());
        } else if (clientAddress.contains("infura.io")) {
            web3j = org.web3j.protocol.Web3j.build(new InfuraHttpService(clientAddress));
        } else if (clientAddress.contains("nodesmith.io")) {
            web3j = org.web3j.protocol.Web3j.build(new NodesmithHttpService(clientAddress));
        } else {
            web3j = org.web3j.protocol.Web3j.build(new HttpService(clientAddress));
        }

        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
            if (web3ClientVersion.hasError()) {
                exitError(
                        "Unable to process response from client: " + web3ClientVersion.getError());
            } else {
                notify(
                        "Connected successfully to client: %s%n",
                        web3ClientVersion.getWeb3ClientVersion());
                return web3j;
            }
        } catch (InterruptedException | ExecutionException e) {
            exitError("Problem encountered verifying client: " + e.getMessage());
        }
        throw new RuntimeException("Application exit failure");
    }
}
