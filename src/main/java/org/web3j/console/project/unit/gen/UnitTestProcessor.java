package org.web3j.console.project.unit.gen;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.com.generate.templates.DeployTemplate;
import org.com.generate.templates.TransferFromTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

public class UnitTestProcessor {
    private final Method method;

    UnitTestProcessor(final Method method) {
        this.method = method;
    }

    MethodSpec getDeploy() {

        return new DeployTemplate(method.getParentClass(), method.getParametersTypes(), unitArguments()).generate();
    }

    MethodSpec getTransferFrom() {
        return new TransferFromTemplate(method.getParentClass(), method.getParametersTypes(), unitArguments()).generate();
    }

    private List<ParameterSpec> unitArguments() {
        List<ParameterSpec> listOfArguments = new ArrayList<>();
        listOfArguments.add(ParameterSpec.builder(Web3j.class, "web3j").build());
        listOfArguments.add(ParameterSpec.builder(TransactionManager.class, "transactionmanager").build());
        listOfArguments.add(ParameterSpec.builder(ContractGasProvider.class, "contractgasprovider").build());
        return listOfArguments;
    }


}
