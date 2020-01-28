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
package org.web3j.console.project.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplateReader {
    public static String readFile(final String name) throws IOException {
        try (final InputStream stream =
                TemplateReader.class.getClassLoader().getResourceAsStream(name)) {
            return readStream(stream);
        }
    }

    private static String readStream(final InputStream stream) throws IOException {

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String temp;
            final StringBuilder stringBuilder = new StringBuilder();
            while ((temp = reader.readLine()) != null) {
                stringBuilder.append(temp).append("\n");
            }
            return stringBuilder.toString();
        }
    }
}
