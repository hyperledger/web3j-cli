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

    private static final Logger log = LoggerFactory.getLogger(<project_name>.class);
    private static final String NODE_URL = "NODE_URL";

    public static void main(String[] args) throws Exception {
        try {
            Credentials credentials = loadCredentials("<wallet_name>");
            Web3j web3j = createWeb3jService("");
            HelloWorld helloWorld = deployHelloWorld(web3j, credentials, new DefaultGasProvider());
            callGreetMethod(helloWorld);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private static Credentials loadCredentials(String walletName) throws IOException, CipherException {
        String pathToProjectResources = String.join(File.separator, System.getProperty("user.dir"), "src", "test", "resources", "wallet");
        String pathToWallet = String.join(File.separator, pathToProjectResources, walletName);
        String pathToWalletPasswordFile = String.join(File.separator, pathToProjectResources, "<password_file_name>");
        File file = new File(pathToWalletPasswordFile);
        log.info("Reading wallet password from resources.");
        String password = new String(Files.readAllBytes(Paths.get(file.toURI())));
        log.info("Loading wallet file: " + walletName + " from resources.");
        log.info("Creating credentials from wallet.");
        return WalletUtils.loadCredentials(password, new File(pathToWallet));
    }

    private static Web3j createWeb3jService(String url) {
        final String nodeURLProperty = System.getProperty(NODE_URL);
        final String nodeURLEnv = System.getenv(NODE_URL);
        if (url == null || url.isEmpty()) {
            if (nodeURLProperty == null || nodeURLProperty.isEmpty()) {
                if (nodeURLEnv == null || nodeURLEnv.isEmpty()) {
                    log.info("Please make sure the node url is valid.");
                    log.info("You can edit the node url programmatically, use java -D" + NODE_URL + "=\"\" or as an environmental variable e.g export " + NODE_URL + "=\"\"");
                    System.exit(1);
                } else {
                    log.info("Connecting to " + nodeURLEnv);
                    return Web3j.build(new HttpService(nodeURLEnv));
                }
            } else {
                log.info("Connecting to " + nodeURLProperty);
                return Web3j.build(new HttpService(nodeURLProperty));
            }
        }
        log.info("Connecting to " + url);
        return Web3j.build(new HttpService(url));
    }

    private static HelloWorld deployHelloWorld(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) throws Exception {
        return HelloWorld.deploy(web3j, credentials, contractGasProvider, "Hello Blockchain World!").send();
    }

    private static void callGreetMethod(HelloWorld helloWorld) throws Exception {
        log.info("Calling the greeting method of contract HelloWorld");
        String response = helloWorld.greeting().send();
        log.info("Contract returned: " + response);
    }
}