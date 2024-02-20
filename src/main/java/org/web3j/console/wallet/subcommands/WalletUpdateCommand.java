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

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.web3j.console.Web3jVersionProvider;
import org.web3j.console.utils.IODevice;
import org.web3j.console.wallet.WalletManager;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.crypto.exception.CipherException;

import static org.web3j.codegen.Console.exitError;

@Command(
        name = "update",
        description = "Update wallet passwords",
        showDefaultValues = true,
        abbreviateSynopsis = true,
        mixinStandardHelpOptions = true,
        versionProvider = Web3jVersionProvider.class,
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        optionListHeading = "%nOptions:%n",
        footerHeading = "%n",
        footer = "Web3j CLI is licensed under the Apache License 2.0")
public class WalletUpdateCommand extends WalletManager implements Runnable {

    @Parameters(
            index = "0",
            paramLabel = "wallet-path",
            description = "Path/filename of the wallet file",
            arity = "1")
    String walletFileLocation;

    public WalletUpdateCommand() {
        super();
    }

    public WalletUpdateCommand(final IODevice console, final String walletFileLocation) {
        super(console);
        this.walletFileLocation = walletFileLocation;
    }

    @Override
    public void run() {
        File walletFile = new File(walletFileLocation);
        Credentials credentials = getCredentials(walletFile);

        notify("Wallet for address " + credentials.getAddress() + " loaded\n");

        String newPassword = getPassword("Please enter a new wallet file password: ");

        String destinationDir = getDestinationDir();
        File destination = createDir(destinationDir);

        try {
            String walletFileName =
                    WalletUtils.generateWalletFile(
                            newPassword, credentials.getEcKeyPair(), destination, true);
            notify(
                    "New wallet file "
                            + walletFileName
                            + " successfully created in: "
                            + destinationDir
                            + "\n");
        } catch (CipherException | IOException e) {
            exitError(e);
        }

        String delete = request("Would you like to delete your existing wallet file (Y/N)? [N]: ");
        if (delete.toUpperCase().equals("Y")) {
            if (!walletFile.delete()) {
                exitError("Unable to remove wallet file\n");
            } else {
                notify("Deleted previous wallet file: %s\n", walletFile.getName());
            }
        }
    }
}
