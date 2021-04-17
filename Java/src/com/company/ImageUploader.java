package com.company;

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
import java.util.Scanner;

public class ImageUploader {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(672197500000000L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(1L);

    private Web3j web3j;
    private Credentials _credentials;
    private UploadImage contract;
    private ArrayList<Image> uploadedImages;

    public ImageUploader(String contractAddress, Credentials credentials) {
        web3j = Web3j.build(new HttpService());
        _credentials = credentials;
        setContract(contractAddress);
        uploadedImages = new ArrayList<>();

        //Sync the blockchain with the local storage
        try {
            imageSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ArrayList<File> imagePaths) {
        try {
            imageSync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double i = 0;
        System.out.println("New Images to upload, checking hashes");

        ArrayList<Image> imagesToUpload = new ArrayList<>();
        for (File imagePath : imagePaths) {
            try {
                byte[] imageHash = getImageHash(imagePath);
                if (uploadedImages.size() == 0 || !isImageHashAlreadyUploaded(imageHash)) {
                    System.out.println("New hash detected. Checking for corruption or new image");
                    Image image = new Image(imageHash, imagePath.getName());
                    if (!checkForCorruption(image)) {
                        System.out.println("Adding new image: " + image.name);
                        if (image.name.equals("IMG_20170312_174324.jpg")) {
                            System.out.println("Debug");
                        }
                        imagesToUpload.add(new Image(imageHash, imagePath.getName()));
                    }
                    else {
                        System.out.println("Warning corruption detected! The hashes do not line up for file: " + image.name);
                    }
                }
                else {
                    System.out.println("Image already stored on blockchain: " + imagePath.getName());
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("    Hash Verification Progress: " + (i / imagePaths.size()) * 100);
        }


        i = 0;
        System.out.println("Sending hashes to blockchain:");
        try {
            for (Image imageHash : imagesToUpload) {
                contract.addImage(imageHash.hash, imageHash.name).send();
                System.out.println("    Hash list upload progress: " + (int)((i / imagesToUpload.size()) * 100) + "%");
                i++;
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

    public byte[] getImageHash(byte[] file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(file);

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

    /**
     * Returns a copy of the uploadedImages ArrayList
     * @return
     */
    public ArrayList<Image> getUploadedImages() {
        return new ArrayList<>(uploadedImages);
    }

    private UploadImage loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return UploadImage.load(contractAddress, web3j, credentials, GAS_Price, GAS_LIMIT);
    }

    private void imageSync() throws Exception {
        System.out.print("Retrieving hash list from Blockchain..............");
        List bytes =  contract.search().send();
        for (int i = 0; i < bytes.size(); i++) {
            byte[] imageBytes = (byte[]) bytes.get(i);
            String imageName = contract.getImageNameFromHash(imageBytes).send();
            uploadedImages.add(new Image(imageBytes, imageName));
            //System.out.println("Found hash: " + formatBytesToHumanReadable((byte[])bytes.get(i)));
        }
        System.out.println("Done");
    }

    public Boolean isImageHashAlreadyUploaded(byte[] hash) {
        for(Image uploadedImage : uploadedImages) {
            if (uploadedImage.hash.length == hash.length) {
                Boolean isEqual = false;
                for (int i = 0; i < hash.length; i++) {
                    if (uploadedImage.hash[i] == hash[i]) {
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

    public Boolean isImageNameUploaded(String name) {
        for (Image image : uploadedImages) {
            if (image.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public Boolean checkForCorruption(Image image){
        //Check if hash is already uploaded
        if (!isImageHashAlreadyUploaded(image.hash)) {
            System.out.println("New hash detected, checking for corruptions");

            //If the hash is not found check if the filename has been uploaded
            if (isImageNameUploaded(image.name)) {
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }

    public void setContract(String contractAddress) {
        Boolean credentialError = true;

        while (credentialError) {
            try {
                contract = loadContract(contractAddress, web3j, _credentials);
                credentialError = false;
            }
            catch (Exception e) {
                System.out.println("Error: invalid contract address.");
                System.out.print("Enter valid contract address: ");
                Scanner in = new Scanner(System.in);
                contractAddress = in.nextLine();
            }
        }
    }
}
