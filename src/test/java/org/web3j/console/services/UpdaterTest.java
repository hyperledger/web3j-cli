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
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.web3j.console.config.ConfigManager;
import org.web3j.console.utils.CliVersion;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.web3j.console.config.ConfigManager.config;

public class UpdaterTest {
    private static WireMockServer wireMockServer;

    @BeforeEach
    void setup() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        ConfigManager.setDevelopment(
                UUID.randomUUID().toString(), CliVersion.getVersion(), null, null, null, false);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {"4.5.6", "4.5.7"})
    void testUpdateCheckWorksSuccessfullyWhenUpdateAvailable(String version) {
        testWorksWithVersion(version, "4.5.6");
    }

    @Test
    void testCurrentVersion() throws Exception {
        String currentVersion = CliVersion.getVersion();
        testWorksWithVersion(currentVersion, currentVersion);
    }

    private void testWorksWithVersion(String version, String currentVersion) {
        String validUpdateResponse =
                String.format(
                        "{\n"
                                + "  \"latest\": {\n"
                                + "    \"version\": \"%s\",\n"
                                + "    \"install_unix\": \"curl -L get.web3j.io | sh\",\n"
                                + "    \"install_win\": \"Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/epirus/epirus-installer/master/installer.ps1'))\"\n"
                                + "  }\n"
                                + "}",
                        version);

        stubFor(
                post(urlPathMatching("/api/epirus/versions/latest"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(validUpdateResponse)));

        Updater.onlineUpdateCheck(
                String.format(
                        "http://localhost:%s/api/epirus/versions/latest", wireMockServer.port()));

        verify(postRequestedFor(urlEqualTo("/api/epirus/versions/latest")));

        if (version.equals(currentVersion)) {
            assertEquals(currentVersion, config.getLatestVersion());
        } else {
            assertEquals(version, config.getLatestVersion());
        }

        wireMockServer.stop();
    }
}
