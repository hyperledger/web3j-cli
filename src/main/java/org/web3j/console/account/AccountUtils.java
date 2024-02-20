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

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.web3j.codegen.Console;
import org.web3j.console.project.wallet.ProjectWallet;
import org.web3j.crypto.exception.CipherException;

public class AccountUtils {
    public static String accountDefaultWalletInit(
            final String defaultWalletPath, final String walletPasswordPath) {
        try {
            ProjectWallet projectWallet = new ProjectWallet(walletPasswordPath, defaultWalletPath);
            final File file =
                    new File(
                            projectWallet.getWalletPath()
                                    + File.separator
                                    + projectWallet.getWalletName());

            System.out.println("Default wallet was created successfully " + file.getAbsolutePath());

            return file.getAbsolutePath();

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CipherException
                | IOException e) {
            Console.exitError("Could not create default wallet reason: " + e.getMessage());
        }
        return "";
    }
}
