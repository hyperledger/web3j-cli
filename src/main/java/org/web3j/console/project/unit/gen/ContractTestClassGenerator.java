package org.web3j.console.project.unit.gen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        listOfValidMethods.forEach(method -> {
            if (method.getName().equals("deploy")) {
                listOfMethodSpecs.add(new UnitTestProcessor(method).getDeploy());
            } else {
                listOfMethodSpecs.add(new UnitTestProcessor(method).getTransferFrom());
            }
        });
        return listOfMethodSpecs;
    }

    void writeClass() throws IOException {
        TypeSpec testClass = TypeSpec.classBuilder(className.getSimpleName() + "Test")
                .addMethods(generateMethodSpecsForEachTest(extractRequiredMethods()))
                .addAnnotation(EVMTest.class)
                .build();

        JavaFile javaFile = JavaFile.builder(className.getPackage().getName(), testClass).build();
        javaFile.writeTo(System.out);
        //   javaFile.writeTo(new File("./src/test/solidity".replace("/", File.separator)));

    }

    private boolean isSupported(Method method) {
        return supportedMethods.contains(method.getName());

    }

    private boolean parametersAreMatching(Method method) {
        if (method.getName().equals("deploy")) {
            return Arrays.asList(method.getParameterTypes()).contains(Web3j.class) && Arrays.asList(method.getParameterTypes()).contains(TransactionManager.class) && Arrays.asList(method.getParameterTypes()).contains(ContractGasProvider.class);
        }
        return true;
    }
}

