package org.web3j.console.project.unit.gen.templates;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.testcontainers.shaded.org.apache.commons.lang.ArrayUtils;

public abstract class UnitTemplate {
    private List<Class> deployArguments;
    private List<ParameterSpec> methodParameters;
    private Class contractName;

    public UnitTemplate(Class contractName, List<Class> deployArguments, List<ParameterSpec> methodParameters) {
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
        return new Object[]{contractName, contractName.getSimpleName().toLowerCase(), contractName};
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
