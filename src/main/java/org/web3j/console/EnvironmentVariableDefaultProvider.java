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
package org.web3j.console;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import picocli.CommandLine.IDefaultValueProvider;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;

import static org.web3j.console.EnvironmentVariablesProperties.*;

public class EnvironmentVariableDefaultProvider implements IDefaultValueProvider {
    private final Map<String, String> environment;

    public EnvironmentVariableDefaultProvider(final Map<String, String> environment) {
        this.environment = environment;
    }

    @Override
    public String defaultValue(final ArgSpec argSpec) {
        if (!argSpec.isOption()) {
            return null;
        }
        return envVarNames((OptionSpec) argSpec)
                .map(environment::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Stream<String> envVarNames(final OptionSpec spec) {
        return Arrays.stream(spec.names())
                .filter(name -> name.startsWith("--")) // Only long options are allowed
                .map(this::fullEnvVarName);
    }

    private String fullEnvVarName(final String name) {
        String suffix = nameToEnvVarSuffix(name);
        String prefix;
        if (Arrays.asList(WEB3J_CLI_SPECIFIC_VARIABLES).contains(suffix)) {
            prefix = WEB3J_CLI_VAR_PREFIX;
        } else if (Arrays.asList(OPENAPI_SPECIFIC_VARIABLES).contains(suffix)) {
            prefix = WEB3J_OPENAPI_VAR_PREFIX;
        } else {
            prefix = WEB3J_VAR_PREFIX;
        }
        return prefix + suffix;
    }

    private String nameToEnvVarSuffix(final String name) {
        return name.substring("--".length()).replace('-', '_').toUpperCase(Locale.US);
    }
}
