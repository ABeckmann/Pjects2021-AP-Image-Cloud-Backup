package com.company;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import sol.UploadImage;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Main {

    private static String PRIVATE_KEY = "";
    private static String CONTRACT_ADDRESS = "";
    private static String _imagesFolder = "";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        String password = "";
        Persistence persistence = new Persistence();

        if (persistence.saveFileExists()) {
            System.out.println("Persistence file detected.");
            while (true) {
                System.out.print("Password: ");
                password = in.nextLine();
                if (persistence.load(password)) {
                    break;
                }
                else {
                    System.out.println();
                    System.out.println("Password incorrect");
                }
            }
        }
        else {
            //Todo: validate these:
            System.out.print("Your Private Key: ");
            PRIVATE_KEY = in.nextLine();

            System.out.print("Smart Contract: ");
            CONTRACT_ADDRESS = in.nextLine();

            System.out.print("Enter image folder: ");
            _imagesFolder = in.nextLine();

            System.out.print("Creating persistence file. Password: ");
            password = in.nextLine();
            persistence.save(password);
        }

        ImageUploader uploader = new ImageUploader(CONTRACT_ADDRESS, getCredentialsFromPrivateKey());
        FileManager fileMan = new FileManager(_imagesFolder, uploader);

        UI ui = new UI(uploader, fileMan, persistence);
        ui.input();

    }

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials("password", "wallet/path");
    }

    /**
     * Get credentials based on a ganache wallet
     * @return
     */
    private static Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }

    public static String getContractAddress() {
        return CONTRACT_ADDRESS;
    }

    public static String getImagesFolder() {
        return  _imagesFolder;
    }

    public static void setPrivateKey(String privateKey) {
        PRIVATE_KEY = privateKey;
    }

    public static void setContractAddress(String contractAddress) {
        CONTRACT_ADDRESS = contractAddress;
    }

    public static void setImagesFolder(String imagesFolder) {
        _imagesFolder = imagesFolder;
    }
}
