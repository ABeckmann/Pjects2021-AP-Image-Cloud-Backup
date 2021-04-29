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

/**
 * @author Alex Pearce 913987
 * This class interacts with the blockchain smart contracts using a Web3J instance to map java calls to solidty.
 */
public class ImageUploader {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(672197500000000L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(1L);

    private Web3j _web3j;
    private Credentials _credentials;
    private UploadImage _contract;
    private ArrayList<Image> _uploadedImages;

    /**
     * Constructor
     * @param contractAddress Ethereum smart contract address
     * @param credentials Credentials generated using an Ethereum private key
     */
    public ImageUploader(String contractAddress, Credentials credentials) {
        _web3j = Web3j.build(new HttpService());
        _credentials = credentials;
        set_contract(contractAddress);
        _uploadedImages = new ArrayList<>();

        //Sync the blockchain with the local storage
        try {
            imageSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload image data(name, hash) to the blockchain.
     * @param imagePaths
     */
    public void upload(ArrayList<File> imagePaths) {
        try {
            imageSync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imagePaths.size() > 0) {
            double i = 0;
            System.out.println("New Images to upload, checking hashes");

            ArrayList<Image> imagesToUpload = new ArrayList<>();

            int corruptionsCount = 0;
            int newImages = 0;
            //For each image check if its already on the blockchain, only upload if it isn't.
            for (File imagePath : imagePaths) {
                byte[] imageHash = getImageHash(imagePath);
                if (_uploadedImages.size() == 0 || !isImageHashAlreadyUploaded(imageHash)) {
                    Image image = new Image(imageHash, imagePath.getName());
                    if (!checkForCorruption(image)) {
                        imagesToUpload.add(new Image(imageHash, imagePath.getName()));
                        newImages++;
                    }
                    else {
                        corruptionsCount++;
                    }
                }
                i++;
                System.out.println("    Hash Verification Progress: " + (int)((i / imagePaths.size()) * 100));
            }


            //Upload the non duplicates
            i = 0;
            System.out.println("Sending hashes to blockchain:");
            try {
                for (Image image : imagesToUpload) {
                    _contract.addImage(image.hash, image.name).send();
                    i++;
                    System.out.println("    Hash list upload progress: " + (int)((i / imagesToUpload.size()) * 100) + "%");
                }
            } catch (RuntimeException ex) {
                System.out.println("Upload failed. Check your private key and the contract address are valid");
            } catch (java.lang.Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Upload complete");
            System.out.println("New images added: " + newImages);
            System.out.println("Corruptions found: " + corruptionsCount);
        }
    }

    /**
     * Computes the hash of a given image
     * @param file image file
     * @return hash as a byte array
     */
    public byte[] getImageHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(Files.readAllBytes(file.toPath()));

            return encodedHash;
        } catch (IOException e) {
            System.out.println("File not found: " + file.getName());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            //This should never throw
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Computes the hash of a file in bytes
     * @param file bytes of a file
     * @return hash bytes as a byte array
     */
    public byte[] getImageHash(byte[] file) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            //This should never throw
            e.printStackTrace();
        }
        byte[] encodedHash = digest.digest(file);
        return encodedHash;
    }

    /**
     * Returns a copy of the uploadedImages ArrayList
     * @return copy of the uploadedImages ArrayList
     */
    public ArrayList<Image> get_uploadedImages() {
        return new ArrayList<>(_uploadedImages);
    }

    /**
     * Loads the contract in Web3J so contract calls can be made
     * @param contractAddress Ethereum contract address
     * @param web3j web3j instance
     * @param credentials generated credentials from an Ethereum private key
     * @return Web3J contract
     */
    private UploadImage loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return UploadImage.load(contractAddress, web3j, credentials, GAS_Price, GAS_LIMIT);
    }

    /**
     * Pulls the uploaded list from the blockchain and saves it locally
     * @throws Exception if there is a problem with the contract execution
     */
    private void imageSync() throws Exception {
        System.out.print("Retrieving hash list from Blockchain..............");
        _uploadedImages = new ArrayList<>();
        List bytes =  _contract.getImageList().send();
        for (int i = 0; i < bytes.size(); i++) {
            byte[] imageBytes = (byte[]) bytes.get(i);
            String imageName = _contract.getImageNameFromHash(imageBytes).send();
            _uploadedImages.add(new Image(imageBytes, imageName));
        }
        System.out.println("Done");
    }

    /**
     * Check if an image hash is already on the blockchain
     * @param hash hash to search for
     * @return true if the hash is on the blockchain
     */
    public Boolean isImageHashAlreadyUploaded(byte[] hash) {
        for(Image uploadedImage : _uploadedImages) {
            if (uploadedImage.hash.length == hash.length) {
                boolean isEqual = false;
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

    /**
     * Checks if an image name is already on the blockchain
     * @param name name of the image file
     * @return true if the name is already on the blockchain
     */
    public Boolean isImageNameUploaded(String name) {
        for (Image image : _uploadedImages) {
            if (image.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Looks for an image corruption based on the hash
     * @param image image to check for corruption
     * @return true if corrupt
     */
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

    /**
     * Sets the contract
     * @param contractAddress Ethereum smart contract address
     */
    public void set_contract(String contractAddress) {
        Boolean credentialError = true;

        //Keep looping until a valid input
        while (credentialError) {
            try {
                _contract = loadContract(contractAddress, _web3j, _credentials);
                credentialError = false;
                Main.setContractAddress(contractAddress);
                Persistence.save();
            }
            catch (Exception e) {
                System.out.println("Error: invalid contract address.");
                System.out.print("Enter valid contract address: ");
                Scanner in = new Scanner(System.in);
                contractAddress = in.nextLine();
            }
        }
    }

    /**
     * Sets the private key
     * @param privateKey Ethereum private key
     */
    public void setPrivateKey(String privateKey) {
        Main.setPrivateKey(privateKey);
        _credentials = Main.getCredentialsFromPrivateKey();
        set_contract(Main.getContractAddress());
    }
}
