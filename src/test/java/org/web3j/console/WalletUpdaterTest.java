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

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.web3j.console.KeyImporterTest.WALLET_PASSWORD;

public class WalletUpdaterTest {
    IODevice console = mock(IODevice.class);

    @Test
    public void testWalletUpdate(@TempDir Path tempDirPath) {
        when(console.readPassword(startsWith("Please enter your existing wallet file password")))
                .thenReturn(WALLET_PASSWORD);

        when(console.readPassword(contains("password")))
                .thenReturn(WALLET_PASSWORD, WALLET_PASSWORD);
        when(console.readLine(startsWith("Please enter a destination directory ")))
                .thenReturn(tempDirPath.toString());
        when(console.readLine(startsWith("Would you like to delete"))).thenReturn("N");

        WalletUpdater.main(
                console,
                KeyImporterTest.class
                        .getResource(
                                "/keyfiles/"
                                        + "UTC--2016-11-03T05-55-06.340672473Z--ef678007d18427e6022059dbc264f27507cd1ffc")
                        .getFile());

        verify(console).printf(contains("successfully created in"));
    }
}
