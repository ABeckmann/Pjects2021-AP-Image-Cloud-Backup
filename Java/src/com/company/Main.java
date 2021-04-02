package com.company;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import sol.UploadImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    private final static String PRIVATE_KEY = "71253e87ec955280f921741b5f45ead1e3e9f1a9551f06717e503ed779cbcdb8";
    private final static String CONTRACT_ADDRESS = "0xF7306f34Cb70C14DB4Fdbb859433f03881988BF0";
    private final static String FILE_NAME = "localDatabase.txt";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(20000000000L);

    public static void main(String[] args) {
        ImageUploader uploader = new ImageUploader();
        FileReaderWriter hashDatabase = new FileReaderWriter(FILE_NAME);

        System.out.print("Enter Image location: ");
        Scanner in = new Scanner(System.in);
        File file = new File("IMG_20200715_002341.jpg");
        String hashString = null;
        try {
            hashString = uploader.getImageHash(file);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new Main(hashString, uploader.getImageHashtest(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Main(String hash, byte[] x) throws Exception {
        Web3j web3j = Web3j.build(new HttpService());
        printWeb3ClientVersion(web3j);

        UploadImage contract = loadContract(CONTRACT_ADDRESS, web3j, getCredentialsFromPrivateKey());

        System.out.print("Sending upload transaction........");

        contract.storeHash("hello world").send();

        for (int i = 0; i < 100; i++) {
            contract.addData(x).send();
        }

        System.out.println("Done");
        System.out.println("Stored hash on chain");


        System.out.println("Retrieving hash from Blockchain");
        //String storedValue = contract.message().send();
        String storedValue = contract.getLength().send().toString();
        System.out.println(storedValue);

        byte[] encodedHash = contract.getData().send();

        StringBuilder sb = new StringBuilder();
        for (byte b : encodedHash) {
            sb.append(String.format("%02X ", b));
        }

        String hashString = new String(sb);
        hashString = hashString.replace(" ", "");
        hashString = "0x" + hashString;
        System.out.println("Generated hash: " + hashString);
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
        return UploadImage.deploy(web3j, credentials, GAS_Price, GAS_LIMIT).send().getContractAddress();
    }

    private UploadImage loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return UploadImage.load(contractAddress, web3j, credentials, GAS_Price, GAS_LIMIT);
    }
}
