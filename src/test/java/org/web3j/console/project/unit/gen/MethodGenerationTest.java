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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodGenerationTest extends Setup {
    private File classAsFile =
            new File(
                    temp
                            + separator
                            + "test"
                            + separator
                            + "src"
                            + separator
                            + "test"
                            + separator
                            + "solidity"
                            + separator
                            + "org"
                            + separator
                            + "com"
                            + separator
                            + "generated"
                            + separator
                            + "contracts"
                            + separator
                            + "TestContract2Test.java");

    @Test
    public void testThatRequiredMethodsAreExtracted() throws FileNotFoundException {

        String classAsString =
                new BufferedReader(new FileReader(classAsFile))
                        .lines()
                        .collect(Collectors.joining("\n"));
        assertTrue(classAsString.contains(" testDeploy"));
    }

    @Test
    public void testThatDeployMethodWasGeneratedCorrectly() throws FileNotFoundException {

        String classAsString =
                new BufferedReader(new FileReader(classAsFile))
                        .lines()
                        .collect(Collectors.joining("\n"));
        String deployTemplate =
                "@BeforeEach\n"
                        + "  public void testDeploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) throws Exception {\n"
                        + "    // Make sure to change the placeholder arguments.;\n"
                        + "    testContract2 = TestContract2.deploy(web3j,transactionManager,contractGasProvider,\"REPLACE_ME\").send();\n"
                        + "  }";
        assertTrue(classAsString.contains(deployTemplate));
    }

    @Test
    public void testThatOnlySupportedMethodsWereGenerated() throws FileNotFoundException {
        String classAsString =
                new BufferedReader(new FileReader(classAsFile))
                        .lines()
                        .collect(Collectors.joining("\n"));
        assertTrue(classAsString.contains("public void testNewGreeting"));
    }
}
