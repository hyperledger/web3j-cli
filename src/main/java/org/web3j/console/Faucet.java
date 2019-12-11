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
package org.web3j.console;

public enum Faucet {
    RINKEBY("Rinkeby", "https://rinkeby.faucet.epirus.io"),
    ROPSTEN("Ropsten", "https://ropsten.faucet.epirus.io");
    //    LOCAL("Local", "http://localhost:8000");

    public final String name;
    public final String url;

    Faucet(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }
}
