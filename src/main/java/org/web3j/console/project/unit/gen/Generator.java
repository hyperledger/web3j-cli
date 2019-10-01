package org.web3j.console.project.unit.gen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Generator {

    public static void main(String[] args) {
        String pathToGeneratedSolidity = "build" +
                File.separator
                + "generated"
                + File.separator
                + "source"
                + File.separator
                + "web3j"
                + File.separator
                + "main" + File.separator
                + "java" + File.separator
                + "org" + File.separator
                + "com" + File.separator
                + "generated"
                + File.separator
                + "contracts";
        File[] generatedContracts = new File(pathToGeneratedSolidity).listFiles();
        assert generatedContracts != null;
        Arrays.stream(generatedContracts).forEach(file -> {
            try {
                new ContractTestClassGenerator(getClassFromFile(file)).writeClass();
            } catch (ClassNotFoundException | IOException e) {
                e.getMessage();
            }

        });


    }

    private static Class getClassFromFile(File javaFile) throws ClassNotFoundException {
        return Class.forName("org.com.generated.contracts." + javaFile.getName().substring(0, javaFile.getName().indexOf(".")));
    }


}
