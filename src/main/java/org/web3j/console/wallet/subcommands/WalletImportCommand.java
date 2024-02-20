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
import java.util.Optional;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.utils.IODevice;
import org.web3j.console.wallet.WalletManager;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.crypto.exception.CipherException;
import org.web3j.utils.Files;

import static org.web3j.codegen.Console.exitError;
import static org.web3j.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;

/** Create Ethereum wallet file from a provided private key. */
@Command(
        name = "import",
        description = "Create wallet from Ethereum private key",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class WalletImportCommand extends WalletManager implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "private-key",
            description = "A hex private-key or a key file path")
    Optional<String> privateKey = Optional.empty();

    public WalletImportCommand() {
        super();
    }

    public WalletImportCommand(final IODevice console) {
        super(console);
    }

    @Override
    public void run() {
        if (privateKey.isPresent()) {
            run(privateKey.get());
        } else {
            String input = request("Please enter the hex encoded private key or key file path: ");
            run(input);
        }
    }

    private void run(String input) {
        File keyFile = new File(input);

        if (keyFile.isFile()) {
            String privateKey = null;
            try {
                privateKey = Files.readString(keyFile);
            } catch (IOException e) {
                exitError("Unable to read file " + input);
            }

            createWalletFile(privateKey.trim());
        } else {
            createWalletFile(input.trim());
        }
    }

    private void createWalletFile(String privateKey) {
        if (!WalletUtils.isValidPrivateKey(privateKey)) {
            exitError(
                    "Invalid private key specified, must be "
                            + PRIVATE_KEY_LENGTH_IN_HEX
                            + " digit hex value");
        }

        Credentials credentials = Credentials.create(privateKey);
        String password = getPassword("Please enter a wallet file password: ");

        String destinationDir = getDestinationDir();
        File destination = createDir(destinationDir);

        try {
            String walletFileName =
                    WalletUtils.generateWalletFile(
                            password, credentials.getEcKeyPair(), destination, true);
            notify(
                    "Wallet file "
                            + walletFileName
                            + " successfully created in: "
                            + destinationDir
                            + "\n");
        } catch (CipherException | IOException e) {
            exitError(e);
        }
    }
}
