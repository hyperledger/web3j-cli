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
package org.web3j.console.project.templates.java.erc777;

import java.io.IOException;

import org.web3j.console.project.templates.java.JavaTemplateProvider;

public class Erc777JavaTemplateProvider extends JavaTemplateProvider {

    private final String tokenName;
    private final String tokenSymbol;
    private final String initialSupply;
    private final String[] defaultProviders;

    protected Erc777JavaTemplateProvider(
            String mainJavaClass,
            String solidityContract,
            String pathToSolidityFolder,
            String gradleBuild,
            String gradleSettings,
            String gradlewWrapperSettings,
            String gradlewBatScript,
            String gradlewScript,
            String gradlewJar,
            String packageNameReplacement,
            String projectNameReplacement,
            String tokenName,
            String tokenSymbol,
            String initialSupply,
            String[] defaultProviders,
            String readme) {
        super(
                mainJavaClass,
                solidityContract,
                pathToSolidityFolder,
                gradleBuild,
                gradleSettings,
                gradlewWrapperSettings,
                gradlewBatScript,
                gradlewScript,
                gradlewJar,
                packageNameReplacement,
                projectNameReplacement,
                readme);
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.initialSupply = initialSupply;
        this.defaultProviders = defaultProviders;
    }

    @Override
    public String loadMainJavaClass() throws IOException {
        return super.loadMainJavaClass()
                .replaceAll("<NAME>", tokenName)
                .replaceAll("<SYMBOL>", tokenSymbol)
                .replaceAll("<INITIAL_SUPPLY>", getInitialSupplyAsCode())
                .replaceAll("<DEFAULT_OPERATORS>", getDefaultOperatorsAsCode());
    }

    private String getInitialSupplyAsCode() {
        return "new BigInteger(\"" + initialSupply + "\")";
    }

    private String getDefaultOperatorsAsCode() {
        if (defaultProviders == null || defaultProviders.length == 0) {
            return "Collections.emptyList()";
        } else {
            return "Arrays.asList(\"" + String.join("\",\"", defaultProviders) + "\")";
        }
    }
}
