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
package org.web3j.console.project.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jetty.io.RuntimeIOException;

public class ClassExecutor {
    public ProcessBuilder executeClassAsSubProcessAndReturnProcess(
            Class classToExecute, List<String> jvmArgs, List<String> args, boolean suppressOutput) {

        ProcessBuilder pb =
                new ProcessBuilder(
                        concatenate(
                                Collections.singletonList(
                                        System.getProperty("java.home")
                                                + File.separator
                                                + "bin"
                                                + File.separator
                                                + "java"),
                                jvmArgs,
                                Collections.singletonList("-cp"),
                                Collections.singletonList(System.getProperty("java.class.path")),
                                Collections.singletonList(classToExecute.getName()),
                                args));

        if (suppressOutput) {
            try {
                pb.redirectError(ProcessBuilder.Redirect.to(File.createTempFile("process", "err")));
                pb.redirectOutput(
                        ProcessBuilder.Redirect.to(File.createTempFile("process", "out")));
            } catch (IOException ex) {
                throw new RuntimeIOException(ex);
            }
        }

        return pb;
    }

    @SafeVarargs
    public static <T> List<T> concatenate(List<T>... lists) {
        return Stream.of(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
