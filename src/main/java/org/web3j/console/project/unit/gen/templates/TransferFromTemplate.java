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

import java.util.List;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Test;

public class TransferFromTemplate extends UnitTemplate {

    private final List<ParameterSpec> methodParameters;

    public TransferFromTemplate(
            final Class contractName,
            final List<Class> deployArguments,
            final List<ParameterSpec> methodParameters) {
        super(contractName, deployArguments);
        this.methodParameters = methodParameters;
    }

    @Override
    public MethodSpec generate() {
        return MethodSpec.methodBuilder("testTransferFrom")
                .addAnnotation(Test.class)
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .returns(TypeName.VOID)
                .addParameters(methodParameters)
                .addStatement("$T $L = $T.transferFrom(" + generatePattern() + ").send()", joined())
                .build();
    }
}
