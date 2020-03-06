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
package org.web3j.console.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import org.web3j.console.config.CliConfig;
import org.web3j.console.telemetry.Telemetry;
import org.web3j.utils.Version;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.mock;

public class TelemetryTest {

    private static WireMockServer wireMockServer;

    @BeforeEach
    void setup(@TempDir Path temp) {
        wireMockServer = new WireMockServer(wireMockConfig().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testExpectedTelemetryWorks() throws IOException {
        CliConfig config =
                mock(
                        CliConfig.class,
                        Mockito.withSettings()
                                .useConstructor(
                                        Version.getVersion(),
                                        "http://localhost:8081",
                                        UUID.randomUUID().toString(),
                                        Version.getVersion(),
                                        null,
                                        null)
                                .defaultAnswer(Mockito.CALLS_REAL_METHODS));

        Telemetry.uploadAnalytics(
                config,
                "--telemetry",
                "wallet",
                "fund",
                "0xceeeefe21b2f2ea5df62ed2efde1e3f1e5540f96");

        stubFor(
                post(urlPathMatching("/api/analytics/"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")));

        verify(
                postRequestedFor(urlEqualTo("/api/analytics"))
                        .withRequestBody(containing("0xceeeefe21b2f2ea5df62ed2efde1e3f1e5540f96"))
                        .withRequestBody(notMatching(".*--telemetry.*")));
    }
}
