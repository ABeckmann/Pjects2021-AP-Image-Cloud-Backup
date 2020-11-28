package com.company;

import jnr.ffi.annotations.Meta;
import kotlin.Experimental;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import sol.MetaCoin;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {

    private final static String PRIVATE_KEY = "9adfdba80295f4a079f18221f084bbe4eddecdfdc059316f3b006d3542d0422c";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(20000000000L);

    private final static String RECIPIENT = "0x1416C11A20ceBC9Ae44cf93F91B147679c760832";

    private final static String CONTRACT_ADDRESS = "0x538A08725E760db70Cdba65443B03ebc275De060";

    public static void main(String[] args) {
        try {
            new Main();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Main() throws Exception {
            Web3j web3j = Web3j.build(new HttpService());

            TransactionManager tansactionManager = new RawTransactionManager(
                    web3j,
                    getCredentialsFromPrivateKey(),
                    (byte)1 //Ethereum chain type (1 is default)
            );

        Transfer transfer = new Transfer(web3j, tansactionManager);

        System.out.print("Sending 1 Eth.......");
        TransactionReceipt transactionReceipt = transfer.sendFunds(
                RECIPIENT,
                BigDecimal.ONE, //amount to send
                Convert.Unit.ETHER,
                GAS_Price,
                GAS_LIMIT
        ).send();
        System.out.print("Sent");
        System.out.println();

        //Deploy the contract (DONT NEED TO DO THIS AS ITS DONE IN TRUFFLE)
        //System.out.println(deployContract(web3j, getCredentialsFromPrivateKey()));


        MetaCoin metaCoin = loadContract(CONTRACT_ADDRESS, web3j, getCredentialsFromPrivateKey());
        System.out.println("Deployed contract address: " + metaCoin.getContractAddress());

        BigInteger n = metaCoin.getBalance("0xB890C4Db778415cF77C8879B4F60f69033532aA9").send();

        BigInteger amount = BigInteger.valueOf(500);

        metaCoin.sendCoin("0x72cF0ee1e78F4C134AaEf420B49590AA70aC0b1E", amount).send();

        n = metaCoin.getBalance("0xB890C4Db778415cF77C8879B4F60f69033532aA9").send();
        System.out.println(n);
        n = metaCoin.getBalance("0x1416C11A20ceBC9Ae44cf93F91B147679c760832").send();
        System.out.println(n);
        n = metaCoin.getBalance("0x26f3169F35e98e3a16bB8d3F050C32E783cd5F80").send();
        System.out.println(n);
        n = metaCoin.getBalance("0x72cF0ee1e78F4C134AaEf420B49590AA70aC0b1E").send();
        System.out.println(n);
    }

    public static void printWeb3ClientVersion(Web3j web3j) {
        Web3ClientVersion clientVersion = null;

        try {
            clientVersion = web3j.web3ClientVersion().send();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String clientVersionString = clientVersion.getWeb3ClientVersion();
        System.out.println("Web3 client version: " + clientVersionString);
    }

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials("password", "wallet/path");
    }

    /**
     * Get credentials based on a ganache wallet
     * @return
     */
    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    private String deployContract (Web3j web3j, Credentials credentials) throws Exception {
        return MetaCoin.deploy(web3j, credentials, GAS_Price, GAS_LIMIT).send().getContractAddress();
    }

    private MetaCoin loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return MetaCoin.load(contractAddress, web3j, credentials, GAS_Price, GAS_LIMIT);
    }
}
