package com.company;

import org.web3j.abi.datatypes.Bool;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileManager {
    private ImageUploader _uploader;
    private ArrayList<String> _replicationLocations;
    private String photosLocation;

    public FileManager(String location, ImageUploader uploader) {
        _uploader = uploader;
        _replicationLocations = new ArrayList<>();
        photosLocation = location;
    }

    /**
     * Checks the images directory for new images and finds corruptions
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void scanPhotos() throws IOException, NoSuchAlgorithmException {
        ArrayList<File> newImagesToUpload = new ArrayList<>();
        ArrayList<File> imageFileList = getImageList();
        ArrayList<Image> uploadedImages = _uploader.getUploadedImages();

        double i = 0;
        System.out.println("Local image scan:");
        for (File image : imageFileList) {
           if (!_uploader.isImageHashAlreadyUploaded(_uploader.getImageHash(image))) {
               newImagesToUpload.add(image);
           }
           System.out.println("    Scan Progress: " + (int)((i / imageFileList.size()) * 100) + "%");
           i++;
        }


        if (newImagesToUpload.size() > 0) {
            _uploader.upload(newImagesToUpload);
        }
        else {
            System.out.println("No new images found");
        }

        if (uploadedImages.size() != imageFileList.size()) {
            Boolean isImageFileFound = false;
            for (Image image : uploadedImages) {
                for (File file : imageFileList) {
                    if (image.name.equals(file.getName())) {
                        isImageFileFound = true;
                        break;
                    }
                }

                if (!isImageFileFound) {
                    System.out.println("Warning: Image file on blockchain but can't be found locally, possible deletion: " + image.name);
                }
                isImageFileFound = false;
            }
        }
    }

    public void refresh() {
        try {
            scanPhotos();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<File> getImageList() {
        File dir = new File(photosLocation);
        File[] directoryListing = dir.listFiles();
        ArrayList<File> imageFiles = new ArrayList<>();
        if (directoryListing != null) {
            for (File image : directoryListing) {
                imageFiles.add(image);
            }
        } else {
            System.out.println("Error folder not found: " + photosLocation);
        }

        return imageFiles;
    }

    public void setPhotosLocation(String photosLocation) {
        this.photosLocation = photosLocation;
        refresh();
    }

    public void exportImages() {
        refresh();
        ArrayList<File> imageFiles = getImageList();

        Scanner in = new Scanner(System.in);
        System.out.print("Export Location: ");
        String exportLocation = in.nextLine();

        System.out.println("Starting Export:");
        System.out.println("    Exporting " + imageFiles.size() + " images");

        String tempFolderName = "temp";
        double i = 0;
        try {
            AES aes = new AES();

            FileWriter writer = new FileWriter(tempFolderName);

            for (File imageFile : imageFiles) {
                String imageName = imageFile.getName();
                String imageHash = Base64.getEncoder().encodeToString(_uploader.getImageHash(imageFile));
                String fileBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(imageFile.getPath())));

                writer.write(imageName + ",");
                writer.write(imageHash + ",");
                writer.write(fileBytes + '\n');

                i++;
                double percentComplete = (i / imageFiles.size()) * 100;
                System.out.println("        Exported: " + percentComplete + "%");
            }
            writer.flush();
            writer.close();

            System.out.println("        Encrypting");
            byte[] fileBytes = Files.readAllBytes(Paths.get(tempFolderName));
            SecretKey key = aes.generateKeyFromPassword(Main.getPrivateKey());
            byte[] cipherText = aes.encrypt(fileBytes, key);

            Files.delete(Paths.get(tempFolderName));
            Files.write(Paths.get(exportLocation), cipherText);

            System.out.println("    Done");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importImages() {
        refresh();

        Scanner in = new Scanner(System.in);
        System.out.print("Import Location: ");
        String importLocation = in.nextLine();

        System.out.println("Starting Import:");

        System.out.println("    Decrypting");
        ArrayList<String> lines = new ArrayList<>();
        try {
            AES aes = new AES();
            byte[] cipherText = Files.readAllBytes(Paths.get(importLocation));

            byte[] fileBytes = aes.decrypt(cipherText, aes.generateKeyFromPassword(Main.getPrivateKey()));

            String plainText = new String(fileBytes);
            String[] split = plainText.split("\n");
            for (int i = 0; i < split.length; i++) {
                lines.add(split[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("    Decrypted");

        try {
            System.out.println("    Found:" + lines.size() + " images");

            String[] split;
            String imageName;
            byte[] imageHash;
            byte[] fileBytes;

            double i = 0;
            System.out.println("Checking export file with local copy");
            for (String line : lines) {
                split = line.split(",");

                imageName = split[0];
                imageHash = Base64.getDecoder().decode(split[1]);
                fileBytes = Base64.getDecoder().decode(split[2]);

                byte[] b = imageHash;
                byte[] b1 = _uploader.getImageHash(fileBytes);
                Boolean z1 = Arrays.equals(b, b1);
                if (Arrays.equals(imageHash, _uploader.getImageHash(fileBytes))) {
                    if (_uploader.isImageHashAlreadyUploaded(imageHash) && _uploader.isImageNameUploaded(imageName)) {
                        File imageFile = searchImageByName(imageName);
                        if (imageFile != null) {
                            if (!Arrays.equals(_uploader.getImageHash(imageFile), imageHash)) {
                                System.out.println("Local file corruption detected, taking the exported copy: " + imageName);
                                Files.write(Paths.get(photosLocation + "/" + imageName), fileBytes);
                            }
                        }
                        else {
                            System.out.println("Local copy of exported image doesn't exist, copying exported image to local storage " + imageName);
                            Files.write(Paths.get(photosLocation + "/" + imageName), fileBytes);
                        }
                    }
                    else {
                        System.out.println("Error exported image is not on the blockchain (Possibly the export has been corrupted): " + imageName);
                    }
                }
                else {
                    System.out.println("Error exported image hash mismatch (Possibly the export has been corrupted): " + imageName);
                }
                System.out.println("    Import progress: " + (int)((i / lines.size()) * 100) + "%");
                i++;
            }
            System.out.println("Finished import");

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public File searchImageByName(String name) {
        ArrayList<File> imagesList = getImageList();

        for (File image : imagesList) {
            if (image.getName().equals(name)) {
                return image;
            }
        }

        //If the image is not found
        return null;
    }


}
