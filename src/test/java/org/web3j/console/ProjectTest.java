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
package org.web3j.console;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.junit.jupiter.api.BeforeEach;

import org.web3j.console.config.ConfigManager;
import org.web3j.console.project.utils.ClassExecutor;
import org.web3j.console.project.utils.Folders;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

public class ProjectTest extends ClassExecutor {
    protected static File workingDirectory = Folders.tempBuildFolder();
    protected String absoluteWalletPath;

    @BeforeEach
    public void createWeb3jProject()
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException,
                    InvalidAlgorithmParameterException, CipherException {
        ConfigManager.setDevelopment();
        final File testWalletDirectory =
                new File(workingDirectory.getPath() + File.separator + "keystore");
        testWalletDirectory.mkdirs();
        absoluteWalletPath =
                testWalletDirectory
                        + File.separator
                        + WalletUtils.generateNewWalletFile("", testWalletDirectory);
        final String[] args = {"new", "-p", "org.com", "-n", "Test", "-o" + workingDirectory};
        int result = new Web3jCommand(System.getenv(), args).parse();
        if (result != 0) {
            throw new RuntimeException("Failed to generate test project");
        }
    }
}
