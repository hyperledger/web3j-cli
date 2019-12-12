package <package_name>;

import <package_name>.generated.contracts.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class <project_name> {

    private static final Logger log=LoggerFactory.getLogger(<project_name>.class);


    public static void main(String[]args)throws Exception{
        Credentials credentials=loadCredentials("<wallet_name>");
        Web3j web3j=createWeb3jService("ETHEREUM_NODE");
        HelloWorld helloWorld=deployHelloWorld(web3j,credentials,new DefaultGasProvider());
        callGreetMethod(helloWorld);
    }

    private static Credentials loadCredentials(String walletName) throws IOException, CipherException {
        String pathToProjectResources = String.join(File.separator, System.getProperty("user.dir"), "src", "test", "resources", "wallet");
        String pathToWallet = String.join(File.separator, pathToProjectResources, walletName);
        String pathToWalletPasswordFile = String.join(File.separator, pathToProjectResources, "<passwod_file_name>");
        File file = new File(pathToWalletPasswordFile);
        log.info("Reading wallet password from resources.");
        String password = new String(Files.readAllBytes(Paths.get(file.toURI())));
        log.info("Loading wallet file: " + walletName + " from resources.");
        log.info("Creating credentials from wallet.");
        return WalletUtils.loadCredentials(password, new File(pathToWallet));
    }

    private static Web3j createWeb3jService(String url){
        log.info("Connecting to "+url);
        return Web3j.build(new HttpService(url));
    }

    private static HelloWorld deployHelloWorld(Web3j web3j,Credentials credentials,ContractGasProvider contractGasProvider)throws Exception{
        return HelloWorld.deploy(web3j,credentials,contractGasProvider,"Hello Blockchain World!").send();
    }

    private static void callGreetMethod(HelloWorld helloWorld)throws Exception{
        log.info("Calling the greeting method of contract HelloWorld");
        String response=helloWorld.greeting().send();
        log.info("Contract returned: "+ response);
    }
}