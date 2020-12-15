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
package org.web3j.console.wrapper;

import java.nio.file.Path;

import picocli.CommandLine.Option;

import static picocli.CommandLine.Help.Visibility.ALWAYS;

public class CredentialsOptions {
    @Option(
            names = {"-w", "--wallet-path"},
            description = "Wallet file path.",
            showDefaultValue = ALWAYS)
    Path walletPath;

    @Option(
            names = {"-k", "--wallet-password"},
            description = "Wallet password.",
            showDefaultValue = ALWAYS)
    String walletPassword = "";

    @Option(
            names = {"-r", "--private-key"},
            description = "Raw hex private key.",
            showDefaultValue = ALWAYS)
    String rawKey = "";

    @Option(
            names = {"-j", "--wallet-json"},
            description = "JSON wallet.",
            showDefaultValue = ALWAYS)
    String json = "";

    public CredentialsOptions() {}

    public CredentialsOptions(
            final Path walletPath,
            final String walletPassword,
            final String rawKey,
            final String json) {
        this.walletPath = walletPath;
        this.walletPassword = walletPassword;
        this.rawKey = rawKey;
        this.json = json;
    }

    public Path getWalletPath() {
        return walletPath;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public String getRawKey() {
        return rawKey;
    }

    public String getJson() {
        return json;
    }
}
