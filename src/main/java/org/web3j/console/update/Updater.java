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
package org.web3j.console.update;

import org.web3j.console.config.CliConfig;

public class Updater {

    private CliConfig config;

    public Updater(CliConfig config) {
        this.config = config;
    }

    public boolean updateCurrentlyAvailable() {
        if (config.isUpdateAvailable()) {
            System.out.println(
                    String.format("A new Web3j update is available: %s", config.getUpdatePrompt()));
            return true;
        }
        return false;
    }

    public void onlineUpdateCheck() {
        //TODO: implement online update check
        System.out.println(config.getClientId());
        System.out.println(config.getVersion());
    }
}
