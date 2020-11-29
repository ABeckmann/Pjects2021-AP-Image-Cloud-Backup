package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class ImageUploader {

    public ImageUploader() {

    }

    public void upload() {
        System.out.print("Enter Image location: ");
        Scanner in = new Scanner(System.in);

        File file = new File(in.nextLine());

    }

    public String getImageHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(Files.readAllBytes(file.toPath()));;

        StringBuilder sb = new StringBuilder();
        for (byte b : encodedHash) {
            sb.append(String.format("%02X ", b));
        }
        String hashString = new String(sb);
        hashString = hashString.replace(" ", "");
        System.out.println("Generated hash: " + hashString);

        return hashString;
    }
}
