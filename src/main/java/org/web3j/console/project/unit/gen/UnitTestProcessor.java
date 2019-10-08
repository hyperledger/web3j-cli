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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import org.web3j.console.project.unit.gen.templates.DeployTemplate;
import org.web3j.console.project.unit.gen.templates.TransferFromTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

public class UnitTestProcessor {
    private final Method method;
    private final Class tClass;

    UnitTestProcessor(final Method method, final Class tClass) {
        this.method = method;
        this.tClass = tClass;
    }

    MethodSpec getMethodSpec() {
        if (method.getName().equals("deploy")) {
            return new DeployTemplate(
                            tClass,
                            Arrays.asList(method.getParameterTypes()),
                            defaultParameterSpecsForEachUnitTest())
                    .generate();
        } else {
            return new TransferFromTemplate(
                            tClass,
                            Arrays.asList(method.getParameterTypes()),
                            defaultParameterSpecsForEachUnitTest())
                    .generate();
        }
    }

    private List<ParameterSpec> defaultParameterSpecsForEachUnitTest() {
        List<ParameterSpec> listOfArguments = new ArrayList<>();
        listOfArguments.add(ParameterSpec.builder(Web3j.class, "web3j").build());
        listOfArguments.add(
                ParameterSpec.builder(TransactionManager.class, "transactionmanager").build());
        listOfArguments.add(
                ParameterSpec.builder(ContractGasProvider.class, "contractgasprovider").build());
        return listOfArguments;
    }
}
