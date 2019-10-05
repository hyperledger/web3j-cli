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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.web3j.EVMTest;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

class ContractTestClassGenerator {
    private final Class className;
    private final List<String> supportedMethods = Arrays.asList("deploy", "transferFrom");

    ContractTestClassGenerator(Class className) {
        this.className = className;
    }

    private List<Method> extractRequiredMethods() {
        List<Method> validMethods = new ArrayList<>();
        Method[] classMethods = className.getMethods();
        for (Method method : classMethods) {
            if (isSupported(method) && parametersAreMatching(method)) {
                validMethods.add(method);
            }
        }

        return validMethods;
    }

    private List<MethodSpec> generateMethodSpecsForEachTest(List<Method> listOfValidMethods) {
        List<MethodSpec> listOfMethodSpecs = new ArrayList<>();
        listOfValidMethods.forEach(
                method ->
                        listOfMethodSpecs.add(
                                new UnitTestProcessor(method, className).getMethodSpec()));
        return listOfMethodSpecs;
    }

    void writeClass(File destination) throws IOException {
        TypeSpec testClass =
                TypeSpec.classBuilder(className.getSimpleName() + "Test")
                        .addMethods(generateMethodSpecsForEachTest(extractRequiredMethods()))
                        .addAnnotation(EVMTest.class)
                        .build();

        JavaFile javaFile = JavaFile.builder(className.getPackage().getName(), testClass).build();
        javaFile.writeTo(System.out);
        System.out.printf("Generated");
        // javaFile.writeTo(destination);
    }

    private boolean isSupported(Method method) {
        return supportedMethods.contains(method.getName());
    }

    private boolean parametersAreMatching(Method method) {
        if (method.getName().equals("deploy")) {
            return Arrays.asList(method.getParameterTypes()).contains(Web3j.class)
                    && Arrays.asList(method.getParameterTypes()).contains(TransactionManager.class)
                    && Arrays.asList(method.getParameterTypes())
                            .contains(ContractGasProvider.class);
        }
        return true;
    }
}
