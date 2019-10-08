/*
 * Copyright 2019 Web3 Labs LTD.
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
package org.web3j.console.project.unit.gen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClassProviderUntiles {
    private final File pathToWalk;

    ClassProviderUntiles(final File pathToWalk) {
        this.pathToWalk = pathToWalk;
    }

    private List<String> getClassPath() {
        List<String> classPath = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(pathToWalk.toURI()))) {

            classPath =
                    walk.filter(Files::isRegularFile)
                            .map(Path::toString)
                            .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return formatClassPath(classPath);
    }

    private List<String> formatClassPath(final List<String> listOfUrl) {
        return listOfUrl.stream()
                .map(
                        s ->
                                s.substring(
                                        s.indexOf("java" + File.separator) + 5,
                                        s.lastIndexOf(".java")))
                .collect(Collectors.toList());
    }

    private CompilerClassLoader compileClasses() throws IOException {
        URL[] classPathURL = new URL[] {pathToWalk.toURI().toURL()};
        Path outputDirectory = Files.createTempDirectory("tmp");
        return new CompilerClassLoader(outputDirectory.toFile(), classPathURL);
    }

    private List<Class> loadClassesToList(final CompilerClassLoader compilerClassLoader) {
        List<String> formattedClassPath = new ArrayList<>();
        List<Class> classList = new ArrayList<>();
        getClassPath().forEach(s -> formattedClassPath.add(s.replace("/", ".")));
        formattedClassPath.forEach(
                s -> {
                    try {
                        classList.add(
                                compilerClassLoader.loadClass(s.replace(File.separator, ".")));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        return classList;
    }

    public final List<Class> getClasses() throws IOException {
        return loadClassesToList(compileClasses());
    }
}
