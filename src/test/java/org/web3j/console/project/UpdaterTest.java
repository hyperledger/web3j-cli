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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import org.web3j.console.config.CliConfig;
import org.web3j.console.project.utils.Folders;
import org.web3j.console.update.Updater;
import org.web3j.utils.Version;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class UpdaterTest {

    private static Path tempWeb3jSettingsPath;
    private static WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        tempWeb3jSettingsPath = Paths.get(Folders.tempBuildFolder().getAbsolutePath(), ".config");
        wireMockServer = new WireMockServer(wireMockConfig().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {"4.5.6", "4.5.7"})
    void testUpdateCheckWorksSuccessfullyWhenUpdateAvailable(String version) throws Exception {
        testWorksWithVersion(version);
    }

    @Test
    void testCurrentVersion() throws Exception {
        testWorksWithVersion(Version.getVersion());
    }

    private void testWorksWithVersion(String version) throws IOException {
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

        doAnswer(
                        invocation -> {
                            String jsonToWrite =
                                    new Gson()
                                            .toJson(
                                                    new CliConfig(
                                                            config.getVersion(),
                                                            config.getServicesUrl(),
                                                            config.getClientId(),
                                                            config.getLatestVersion(),
                                                            config.getUpdatePrompt(),
                                                            null));
                            Files.write(
                                    tempWeb3jSettingsPath,
                                    jsonToWrite.getBytes(Charset.defaultCharset()));
                            return null;
                        })
                .when(config)
                .save();

        assertFalse(config.isUpdateAvailable());

        Updater updater = new Updater(config);

        String validUpdateResponse =
                String.format(
                        "{\n"
                                + "  \"latest\": {\n"
                                + "    \"version\": \"%s\",\n"
                                + "    \"install_unix\": \"curl -L get.web3j.io | sh\",\n"
                                + "    \"install_win\": \"Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/web3j/web3j-installer/master/installer.ps1'))\"\n"
                                + "  }\n"
                                + "}",
                        version);

        stubFor(
                post(urlPathMatching("/api/versions/latest"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(validUpdateResponse)));
        updater.onlineUpdateCheck();

        verify(postRequestedFor(urlEqualTo("/api/versions/latest")));
        // if the version parameter does not equal config.getVersion, isUpdateAvailable should
        // return true, otherwise it should return false
        assertEquals(!version.equals(config.getVersion()), config.isUpdateAvailable());

        CliConfig realConfigAfterUpdate = CliConfig.getConfig(tempWeb3jSettingsPath.toFile());
        assertEquals(
                !version.equals(config.getVersion()), realConfigAfterUpdate.isUpdateAvailable());
        wireMockServer.stop();
    }
}
