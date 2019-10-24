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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.web3j.codegen.Console;

class ClassProvider {
    private final File pathToWalk;

    ClassProvider(final File pathToWalk) {
        this.pathToWalk = pathToWalk;
    }

    private List<String> getFormattedClassPath() {
        List<String> classPath = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(pathToWalk.toURI()))) {

            classPath =
                    walk.filter(Files::isRegularFile)
                            .map(Path::toString)
                            .collect(Collectors.toList());

        } catch (IOException e) {
            Console.exitError(
                    "Looks like there is a problem with the classpath. Please use Web3j CLI to generate your project.");
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

    private CompilerClassLoader compileClasses() throws MalformedURLException {
        URL[] classPathURL = new URL[] {pathToWalk.toURI().toURL()};
        Path outputDirectory = null;
        try {
            outputDirectory = Files.createTempDirectory("tmp");
        } catch (IOException e) {
            Console.exitError(
                    "Could not create temporary directory to store classes to be loaded.");
        }

        return new CompilerClassLoader(
                Objects.requireNonNull(outputDirectory).toFile(), classPathURL);
    }

    private List<Class> loadClassesToList(final CompilerClassLoader compilerClassLoader) {
        List<String> formattedClassPath = new ArrayList<>();
        List<Class> classList = new ArrayList<>();
        getFormattedClassPath().forEach(s -> formattedClassPath.add(s.replace("/", ".")));
        for (String s : formattedClassPath) {
            try {
                classList.add(compilerClassLoader.loadClass(s.replace(File.separator, ".")));
            } catch (ClassNotFoundException e) {
                Console.exitError("Could not load " + e.getMessage() + " class");
            }
        }
        return classList;
    }

    final List<Class> getClasses() {
        try {
            return loadClassesToList(compileClasses());
        } catch (MalformedURLException e) {
            Console.exitError("Could not get the URL of the files.");
        }
        return Collections.emptyList();
    }
}
