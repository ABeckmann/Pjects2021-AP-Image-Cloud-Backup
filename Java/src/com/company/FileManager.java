package com.company;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Alex Pearce 913987
 * This class manages to local image directory
 */
public class FileManager {
    private final ImageUploader _uploader;
    private final String photosLocation;
    private final String[] FILE_TYPES = {"png", "jpeg", "jpg", "raw"};

    /**
     * Constructor
     * @param location location of the images
     * @param uploader instance of ImageUploader that interacts with the blockchain
     */
    public FileManager(String location, ImageUploader uploader) {
        _uploader = uploader;
        photosLocation = location;
    }

    /**
     * Checks the local images and looks for missing/corrupt/new.
     */
    public void scanPhotos() {

        ArrayList<File> newImagesToUpload = new ArrayList<>();
        ArrayList<File> imageFileList = getImageList();

        //Scan images folder and check if each image is already on the blockchain.
        double i = 0;
        System.out.println("Local image scan:");
        for (File image : imageFileList) {
           if (!_uploader.isImageHashAlreadyUploaded(_uploader.getImageHash(image))) {
               newImagesToUpload.add(image);
           }
            i++;
           System.out.println("    Scan Progress: " + (int)((i / imageFileList.size()) * 100) + "%");
        }

        //Upload any image hashes that are not on the blockchain
        if (newImagesToUpload.size() > 0) {
            _uploader.upload(newImagesToUpload);
        }
        else {
            System.out.println("No new images found");
        }

        //Alert the user if images are missing
        int missingFiles = getMissingFileNames().size();
        if (missingFiles > 0) {
            System.out.println("Detected " + missingFiles +" missing files. Use command \"missing\" to list missing images");
        }
    }

    /**
     * Executes refresh command
     */
    public void refresh() {
        scanPhotos();
    }

    /**
     * Returns the overall image files status
     * @return String with image stats.
     */
    public String status() {
        String output = "";
        output = output + "Number of local images:          " + getImageList().size() + '\n';
        output = output + "Number of uploaded image hashes: " + _uploader.get_uploadedImages().size() + '\n';
        output = output + "Number of missing local files:   " + getMissingFileNames().size();
        return output;
    }

    /**
     * Searches for missing files
     * @return a list of missing file names
     */
    public ArrayList<String> getMissingFileNames() {
        ArrayList<Image> uploadedImages = _uploader.get_uploadedImages();
        ArrayList<File> imageFileList = getImageList();
        ArrayList<String> missingFileNames = new ArrayList<>();

        //If the size is the same then none are missing
        if (uploadedImages.size() != imageFileList.size()) {
            boolean isImageFileFound = false;

            //For each image hash on the blockchain search the local storage for it. Add it to the list if its missing
            for (Image image : uploadedImages) {
                for (File file : imageFileList) {
                    if (image.name.equals(file.getName())) {
                        isImageFileFound = true;
                        break;
                    }
                }
                if (!isImageFileFound) {
                    missingFileNames.add(image.name);
                }
                isImageFileFound = false;
            }
        }

        return missingFileNames;
    }

    /**
     * Searches the local images
     * @return list of all Files in the selected images directory
     */
    public ArrayList<File> getImageList() {
        File dir = new File(photosLocation);
        File[] dirList = dir.listFiles();
        ArrayList<File> imageFiles = new ArrayList<>();
        if (dirList != null) {
            for (File image : dirList) {
                if (isValidFileType(image.getName())) {
                    imageFiles.add(image);
                }
            }
        } else {
            System.out.println("Error folder not found: " + photosLocation);
        }

        return imageFiles;
    }

    /**
     * Extracts the file extension
     * @param fileName
     * @return string of the filetype e.g "txt"
     */
    private String getFileExtension(String fileName) {
        //Regex split ONLY at the last "." e.g "Hello.World.txt" results in {"Hello.World", "txt"}
        String[] split = fileName.split("\\.(?=[^\\.]+$)");

        return split[split.length - 1];
    }

    /**
     * checks if its a valid image filetype.
     * @param fileName
     * @return true of valid image filetype
     */
    private boolean isValidFileType(String fileName) {
        String fileType = getFileExtension(fileName);
        for (String type : FILE_TYPES) {
            if (fileType.equals(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Exports all the images (name, hash, image data) to a single encrypted file.
     */
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

            //Write all the data to an unencrypted temp file.
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

            //Encrypt the temp file and move the data to the desired location
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

    /**
     * Reads and exported file and decrypts the images, then copies them to the images directory.
     */
    public void importImages() {
        refresh();

        Scanner in = new Scanner(System.in);
        System.out.print("Import Location: ");
        String importLocation = in.nextLine();

        System.out.println("Starting Import:");

        System.out.println("    Decrypting");
        ArrayList<String> lines = new ArrayList<>();
        try {
            //Decrypt file
            AES aes = new AES();
            byte[] cipherText = Files.readAllBytes(Paths.get(importLocation));
            byte[] fileBytes = aes.decrypt(cipherText, aes.generateKeyFromPassword(Main.getPrivateKey()));

            //Read the csv data so it can be used later
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

                //Verify the image data using the hash stored in the exported file.
                if (Arrays.equals(imageHash, _uploader.getImageHash(fileBytes))) {

                    //Check image is on the blockchain
                    if (_uploader.isImageHashAlreadyUploaded(imageHash) && _uploader.isImageNameUploaded(imageName)) {
                        File imageFile = searchImageByName(imageName);

                        //Check for corruption
                        if (imageFile != null) {
                            if (!Arrays.equals(_uploader.getImageHash(imageFile), imageHash)) {
                                System.out.println("Local file corruption detected, taking the exported copy: " + imageName);
                                Files.write(Paths.get(photosLocation + "/" + imageName), fileBytes);
                            }
                        }
                        //check for missing local copy
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
                i++;
                System.out.println("    Import progress: " + (int)((i / lines.size()) * 100) + "%");
            }
            System.out.println("Finished import");

        } catch (IOException e) {
            System.out.println("Unable to import data: wrong file type for it may be corrupted");
            e.printStackTrace();
        }
    }

    /**
     * Searches the local images for a given filename
     * @param name name of image file
     * @return File with matching name. Null if image isn't found
     */
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
