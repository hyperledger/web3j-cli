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
package org.web3j.console.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class CliVersion {

    private CliVersion() {}

    public static final String DEFAULT = "none";

    private static final String TIMESTAMP = "timestamp";
    private static final String VERSION = "version";

    public static String getVersion() throws IOException {
        return loadProperties().getProperty(VERSION);
    }

    public static String getTimestamp() throws IOException {
        return loadProperties().getProperty(TIMESTAMP);
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        File propertiesFile = Paths.get("gradle.properties").toFile();
        InputStream is =
                propertiesFile.exists()
                        ? new FileInputStream(propertiesFile)
                        : CliVersion.class.getResourceAsStream("/web3j-version.properties");
        properties.load(is);
        return properties;
    }
}
