package com.company;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import sol.UploadImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private final static String PRIVATE_KEY = "6ea31140f1f463a2523ed296567cfea581d8fd91225c58116eec469d4b88b237";
    private final static String CONTRACT_ADDRESS = "0x2E092DAE686c5656469f640158E1C29aeB7Ec826";

    public static void main(String[] args) {
        ImageUploader uploader = new ImageUploader(CONTRACT_ADDRESS, getCredentialsFromPrivateKey());

        System.out.print("Enter Image location: ");
        Scanner in = new Scanner(System.in);
        //File file = new File("IMG_20200715_002341.jpg");
        String fileString = in.nextLine();
        File file = new File(fileString);

        ArrayList<File> images = new ArrayList<>();
        images.add(file);

        uploader.upload(images);
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
}
