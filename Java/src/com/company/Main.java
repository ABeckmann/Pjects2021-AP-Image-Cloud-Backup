package com.company;

import org.web3j.crypto.Credentials;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author Alex Pearce 913987
 * Main class thats start the system then hands off the UI
 */
public class Main {

    private static String PRIVATE_KEY = "";
    private static String CONTRACT_ADDRESS = "";
    private static String _imagesFolder = "";

    /**
     * Start up and load from a persistence file if there is one otherwise it prompts the user to create one.
     * @param args no args used
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        String password;

        Boolean importedPersistenceFile = false;
        if (Persistence.saveFileExists()) {
            System.out.println("Persistence file detected.");

            //Loop until correct input or the user want to reset everything
            while (true) {
                System.out.print("Password: ");
                password = in.nextLine();
                if (Persistence.load(password)) {
                    importedPersistenceFile = true;
                    break;
                }
                else {
                    System.out.println();
                    System.out.println("Password incorrect");
                    System.out.println("Do you want to reset the system (\"y\" or \"n\")? You will have to re-enter your private key and the contract address");
                    if (in.nextLine().equals("y")) {
                        importedPersistenceFile = false;
                        System.out.println("Resetting");
                        break;
                    }
                }
            }
        }

        //first time setup or after the user forgot the password and triggered a reset
        if (!importedPersistenceFile) {
            System.out.print("Your Private Key: ");
            PRIVATE_KEY = in.nextLine();

            System.out.print("Smart Contract: ");
            CONTRACT_ADDRESS = in.nextLine();

            setImagesFolder();

            System.out.println("Creating persistence file.");
            System.out.print("Create password: ");
            password = in.nextLine();
            Persistence.save(password);
        }


        ImageUploader uploader = new ImageUploader(CONTRACT_ADDRESS, getCredentialsFromPrivateKey());
        FileManager fileMan = new FileManager(_imagesFolder, uploader);
        UI ui = new UI(uploader, fileMan);
        ui.input();

    }

    /**
     * Sets the location of the local images and validates the folder exists
     */
    public static void setImagesFolder() {
        Scanner in = new Scanner(System.in);

        System.out.print("Images folder: ");
        _imagesFolder = in.nextLine();
        while (!Files.exists(Paths.get(_imagesFolder))) {
            System.out.println("Error: Images file doesnt exist do you want to create it (\"y\" or \"n\")?");
            if (in.nextLine().equals("y")) {
                try {
                    Files.createDirectories(Paths.get(_imagesFolder));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Image folder: ");
                _imagesFolder = in.nextLine();
            }
        }
    }

    /**
     * Get credentials based on a ganache wallet
     * @return
     */
    public static Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    /**
     * Get Ethereum private key string
     * @return
     */
    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }

    /**
     * Sets Ethereum private key string
     * @param privateKey
     */
    public static void setPrivateKey(String privateKey) {
        PRIVATE_KEY = privateKey;
    }

    /**
     * Gets Ethereum contract address
     * @return
     */
    public static String getContractAddress() {
        return CONTRACT_ADDRESS;
    }

    /**
     * Sets Ethereum contract address
     * @param contractAddress
     */
    public static void setContractAddress(String contractAddress) {
        CONTRACT_ADDRESS = contractAddress;
    }

    /**
     * Gets local images folder
     * @return
     */
    public static String getImagesFolder() {
        return  _imagesFolder;
    }

    /**
     * Sets local images folder
     * @param imagesFolder
     */
    public static void setImagesFolder(String imagesFolder) {
        _imagesFolder = imagesFolder;

    }
}
