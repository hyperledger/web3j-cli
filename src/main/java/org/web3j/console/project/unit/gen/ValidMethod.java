package org.web3j.console.project.unit.gen;

import java.util.Arrays;
import java.util.List;

public class ValidMethod {
    private String parentClassName;
    private String methodName;
    private Class returnType;
    private List<Class> parametersTypes;


    private Class parentClass;

    ValidMethod(Class parentClass, String methodName, Class returnType, Class[] parametersTypes) {
        this.parentClassName = parentClass.getSimpleName();
        this.methodName = methodName;
        this.returnType = returnType;
        this.parametersTypes = Arrays.asList(parametersTypes);
        this.parentClass = parentClass;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class getReturnType() {
        return returnType;
    }

    List<Class> getParametersTypes() {
        return parametersTypes;
    }

    Class getParentClass() {
        return parentClass;
    }
}
