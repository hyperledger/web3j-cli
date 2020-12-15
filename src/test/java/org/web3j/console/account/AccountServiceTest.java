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

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.web3j.console.config.ConfigManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {
    static AccountService accountService;

    @BeforeAll
    public static void setUp() throws IOException {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        accountService = new AccountService(wireMockServer.baseUrl());
        ConfigManager.setDevelopment();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testAccountConfirmation(boolean active) throws IOException, InterruptedException {
        stubFor(
                get(urlPathMatching("/api/users/status/.*"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(String.format("{\"active\": \"%s\"}", active))));
        assertEquals(active, accountService.checkIfAccountIsConfirmed(1));
    }

    @Test
    public void testAccountCreation() {
        stubFor(
                post(urlPathMatching("/api/users/create/"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\n"
                                                        + "    \"token\": \"8190c700-1f10-4c50-8bb2-1ce78bf0412b\"\n"
                                                        + "}")));

        assertTrue(accountService.createAccount("test@gmail.com"));
    }

    @Test
    public void testAccountAuthenticate() {
        stubFor(
                post(urlPathMatching("/api/users/authenticate/"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\n"
                                                        + "    \"token\": \"8190c700-1f10-4c50-8bb2-1ce78bf0412b\"\n"
                                                        + "}")));

        assertTrue(accountService.authenticate("test@gmail.com", "password"));
    }
}
