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
package org.web3j.console.services;

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.web3j.console.config.ConfigManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class TelemetryTest {

    private static WireMockServer wireMockServer;

    @BeforeEach
    void setup() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        ConfigManager.setDevelopment();

        stubFor(
                post(urlPathMatching("/api/analytics/"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testExpectedTelemetryWorks() {
        Telemetry.uploadTelemetry(
                String.format("%s/api/analytics", wireMockServer.baseUrl()),
                new String[] {
                    "--telemetry", "wallet", "fund", "0xceeeefe21b2f2ea5df62ed2efde1e3f1e5540f96"
                });

        verify(
                postRequestedFor(urlEqualTo("/api/analytics"))
                        .withRequestBody(containing("0xceeeefe21b2f2ea5df62ed2efde1e3f1e5540f96"))
                        .withRequestBody(notMatching(".*--telemetry.*")));
    }

    @Test
    public void testFewerArgsWorks() {
        Telemetry.uploadTelemetry(
                String.format("%s/api/analytics", wireMockServer.baseUrl()),
                new String[] {"--telemetry", "version"});

        verify(
                postRequestedFor(urlEqualTo("/api/analytics"))
                        .withRequestBody(containing("version"))
                        .withRequestBody(notMatching(".*--telemetry.*")));
    }

    @Test
    public void testNoArgsWorks() {
        Telemetry.uploadTelemetry(
                String.format("%s/api/analytics", wireMockServer.baseUrl()),
                new String[] {"--telemetry"});

        verify(
                postRequestedFor(urlEqualTo("/api/analytics"))
                        .withRequestBody(containing("No args"))
                        .withRequestBody(notMatching(".*--telemetry.*")));
    }
}
