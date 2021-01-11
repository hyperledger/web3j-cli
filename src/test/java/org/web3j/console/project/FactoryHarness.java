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
package org.web3j.console.project;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;

import picocli.CommandLine;

public class FactoryHarness {

    public static CommandLine.IFactory getFactory(
            final ByteArrayInputStream inputStream, final PrintStream printStream) {
        return new CommandLine.IFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <K> K create(Class<K> clazz) throws Exception {
                try {
                    final Constructor<?>[] constructors = clazz.getConstructors();
                    for (int i = 0; i < constructors.length; ++i) {
                        if (constructors[i].getParameterCount() == 2
                                && InputStream.class.isAssignableFrom(
                                        constructors[i].getParameterTypes()[0])
                                && PrintStream.class.isAssignableFrom(
                                        constructors[i].getParameterTypes()[1])) {
                            return (K) constructors[i].newInstance(inputStream, printStream);
                        }
                    }
                } catch (Exception e) {
                    return CommandLine.defaultFactory().create(clazz); // fallback if missing
                }
                return CommandLine.defaultFactory().create(clazz); // fallback if missing
            }
        };
    }
}
