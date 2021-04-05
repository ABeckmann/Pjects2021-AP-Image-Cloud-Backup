package com.company;

import org.web3j.abi.datatypes.Bool;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import sol.UploadImage;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ImageUploader {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(20000000000L);

    private Web3j web3j;
    private UploadImage contract;
    private ArrayList<byte[]> uploadedImages;

    public ImageUploader(String contractAddress, Credentials credentials) {
        web3j = Web3j.build(new HttpService());
        contract = loadContract(contractAddress, web3j, credentials);
        uploadedImages = new ArrayList<>();

        //Sync the blockchain with the local storage
        try {
            imageSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ArrayList<File> imagePaths) {
        ArrayList<byte[]> imagesToUpload = new ArrayList<>();
        for (File imagePath : imagePaths) {
            try {
                byte[] imageHash = getImageHash(imagePath);
                if (uploadedImages.size() == 0 || !isImageAlreadyUploaded(imageHash)) {
                    imagesToUpload.add(imageHash);
                }
                else {
                    System.out.println("Image already stored on blockchain: " + imagePath.getName());
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            for (byte[] imageHash : imagesToUpload) {
                System.out.print("Sending upload transaction........");
                contract.addImage(imageHash).send();
                System.out.println("Done");
                System.out.println("Stored hash on chain");
            }
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace();
        }
    }

    public byte[] getImageHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(Files.readAllBytes(file.toPath()));

        return encodedHash;
    }

    public String formatBytesToHumanReadable(byte[] bytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        String hashString = new String(sb);
        hashString = hashString.replace(" ", "");
        hashString = "0x" + hashString;

        return hashString;
    }

    public ArrayList<byte[]> getUploadedImages() {
        return uploadedImages;
    }

    private UploadImage loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return UploadImage.load(contractAddress, web3j, credentials, GAS_Price, GAS_LIMIT);
    }

    private void imageSync() throws Exception {
        System.out.println("Retrieving hash from Blockchain..............");
        List bytes =  contract.search().send();
        for (int i = 0; i < bytes.size(); i++) {
            uploadedImages.add((byte[]) bytes.get(i));
            System.out.println("Found hash: " + formatBytesToHumanReadable((byte[])bytes.get(i)));
        }
    }

    private Boolean isImageAlreadyUploaded(byte[] hash) {

        for(byte[] uploadedImage : uploadedImages) {
            if (uploadedImage.length == hash.length) {
                Boolean isEqual = false;
                for (int i = 0; i < hash.length; i++) {
                    if (uploadedImage[i] == hash[i]) {
                        isEqual = true;
                    }
                    else {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    return true;
                }
            }
        }

        return false;
    }
}
