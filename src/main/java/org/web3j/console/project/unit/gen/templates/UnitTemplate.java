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
package org.web3j.console.project.unit.gen.templates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.testcontainers.shaded.org.apache.commons.lang.ArrayUtils;

public abstract class UnitTemplate {
    private List<Class> deployArguments;
    private List<ParameterSpec> methodParameters;
    private Class contractName;

    public UnitTemplate(
            Class contractName, List<Class> deployArguments, List<ParameterSpec> methodParameters) {
        this.contractName = contractName;
        this.deployArguments = deployArguments;
        this.methodParameters = methodParameters;
    }

    public abstract MethodSpec generate();

    protected Object[] convertTypeNameToLowerCase() {
        return deployArguments.stream().map(this::test).toArray();
    }

    protected String generatePattern() {
        List<String> generated = new ArrayList<>();
        for (Class type : deployArguments) {
            if (type.equals(String.class)) {
                generated.add("$S ");
            } else if (type.equals(BigInteger.class)) {
                generated.add("$T.ONE");
            } else {
                generated.add("$L ");
            }
        }
        return String.join(",", generated);
    }

    protected Object[] staticElements() {
        return new Object[] {
            contractName, contractName.getSimpleName().toLowerCase(), contractName
        };
    }

    protected Object[] joined() {
        return ArrayUtils.addAll(staticElements(), convertTypeNameToLowerCase());
    }

    protected Object test(Class classToCheck) {
        if (classToCheck.equals(String.class)) {
            return "REPLACE_ME";
        } else if (classToCheck.equals(BigInteger.class)) {
            return BigInteger.class;
        } else {
            return classToCheck.getSimpleName().toLowerCase();
        }
    }
}
