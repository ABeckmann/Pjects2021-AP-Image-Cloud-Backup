package com.company;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Persistence {
    private String _persistenceFileName = "persistance";


    public Boolean saveFileExists() {
        File file = new File(_persistenceFileName);

        return file.exists();
    }

    public void save(String password) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Boolean load(String password) {
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

            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }
}
