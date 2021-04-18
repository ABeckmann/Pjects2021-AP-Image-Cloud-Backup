package com.company;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Alex Pearce 913987
 *
 * Handles Saving/Loading persistant data(private key, contract address, images directory).
 */
public class Persistence {
    private static String _persistenceFileName = "persistence";
    private static String _password;


    /**
     * Checks of the file exists
     * @return
     */
    public static Boolean saveFileExists() {
        File file = new File(_persistenceFileName);
        return file.exists();
    }

    /**
     * Saves the data to the file and encrypts it
     * @param password
     */
    public static void save(String password) {
        try {
            AES aes = new AES();
            SecretKey key = aes.generateKeyFromPassword(password);

            String privateKey = Main.getPrivateKey();
            String smartContractAddress = Main.getContractAddress();
            String localImagesFolder = Main.getImagesFolder();

            String fileString = privateKey + ",";
            fileString = fileString + smartContractAddress + ",";
            fileString = fileString + localImagesFolder;

            byte[] fileBytes = fileString.getBytes();

            byte[] cipherText = aes.encrypt(fileBytes, key);

            Files.write(Paths.get(_persistenceFileName), cipherText);
            _password = password;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If the file has already been saved the password is cached so no password parameter required.
     */
    public static void save() {
        if (_password != null) {
            save(_password);
        }
        else {
            System.out.println("Unable to save: no password given");
        }
    }

    /**
     * Loads the file data. Decrypts it then imports the data.
     * @param password
     * @return
     */
    public static Boolean load(String password) {
        if (saveFileExists()) {
            try {
                AES aes = new AES();
                SecretKey key = aes.generateKeyFromPassword(password);

                byte[] cipherText = Files.readAllBytes(Paths.get(_persistenceFileName));

                byte[] fileBytes = aes.decrypt(cipherText, key);

                String fileText = new String(fileBytes);

                String[] split = fileText.split(",");

                if (split.length != 3) {
                    return false;
                }

                Main.setPrivateKey(split[0]);
                Main.setContractAddress(split[1]);
                Main.setImagesFolder(split[2]);

                _password = password;

            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }
}
