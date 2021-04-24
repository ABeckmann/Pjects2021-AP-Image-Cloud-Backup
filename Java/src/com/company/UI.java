package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Alex Pearce 913987
 * Handles most of the input and output to the user.
 */
public class UI {
    private final ImageUploader _uploader;
    private final FileManager _fileManager;

    /**
     * Constructor
     * @param uploader instance of ImageUploader
     * @param fileManager instance of FileManager
     */
    public UI(ImageUploader uploader, FileManager fileManager) {
        _uploader = uploader;
        _fileManager = fileManager;
    }

    /**
     * Main loop of the system. Allows the user to interact with the system and loops forever until "exit" is typed
     */
    public void input() {
        //Clear console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        Scanner in = new Scanner(System.in);
        System.out.println("Image verification system");
        System.out.println();

        String input;
        loop: while (true) {
            System.out.println("Enter Command: (type \"help\" for a list of commands)");
            input = in.nextLine().toLowerCase();

            switch (input) {
                case "help":
                    printHelp();
                    break;
                case "refresh":
                    _fileManager.refresh();
                    break;
                case "status":
                    System.out.println();
                    System.out.println(_fileManager.status());
                    break;
                case "local":
                    System.out.println();
                    ArrayList<File> localImages = _fileManager.getImageList();
                    System.out.println("Local image list:");
                    for (File image : localImages) {
                        System.out.println(image.getName());
                    }
                    System.out.println();
                    break;
                case "uploaded":
                    System.out.println();
                    ArrayList<Image> uploadedImages = _uploader.get_uploadedImages();
                    System.out.println("Uploaded image list:");
                    for (Image image : uploadedImages) {
                        System.out.println(image.name);
                    }
                    System.out.println();
                    break;
                case "missing":
                    System.out.println();
                    ArrayList<String> missingImages = _fileManager.getMissingFileNames();
                    System.out.println("Missing images list:");
                    for (String name : missingImages) {
                        System.out.println(name);
                    }
                    System.out.println();
                    break;
                case "export":
                    _fileManager.exportImages();
                    break;
                case "import":
                    _fileManager.importImages();
                    break;
                case "set":
                    printSet();
                    break;
                case "exit":
                    break loop;

            }

        }
    }

    /**
     * Prints the "help" command to the user
     */
    public void printHelp() {
        System.out.println();
        System.out.println("Commands:");
        System.out.println(" \"refresh\":  Scans the local images and checks for corruptions and adds new images to the blockchain");
        System.out.println(" \"status\":   Prints out the current status");
        System.out.println(" \"local\":    Prints out the list of local images");
        System.out.println(" \"uploaded\": Prints out the list of uploaded images");
        System.out.println(" \"missing\":  Prints out the list of missing or corrupt images");
        System.out.println(" \"export\":   Encrypt and export the images to the specified file. This acts as a secure backup for the local images.");
        System.out.println(" \"import\":   Decrypts and verifies the images based on the blockchain. Use this to recover deleted or corrupted images");
        System.out.println(" \"set\":      Opens submenu to set the following parameters: (Private key, contract address, images folder)");
        System.out.println(" \"exit\":     Closes the program");
    }

    /**
     * Handles the "set" command
     */
    public void printSet() {
        //Clear console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        Scanner in = new Scanner(System.in);

        System.out.println("1: Private key: ");
        System.out.println("2: contract Address: ");
        System.out.println("3: Images Folder: ");
        System.out.println("4: Password: ");
        System.out.print("Option: ");

        String input = in.nextLine().toLowerCase();

        switch (input) {
            case "1":
                System.out.println("Warning: all current local non-blockchain data will be lost, this will wipe your current private key");
                while (true) {
                    try {
                        System.out.print("Enter new Private key: ");
                        input = in.nextLine();
                        _uploader.setPrivateKey(input);
                        break;
                    }
                    catch (Exception e) {
                        System.out.println("Error: Invalid private key.");
                    }
                }
                break;
            case "2":
                System.out.print("Enter new contract address: ");
                input = in.nextLine();
                _uploader.set_contract(input);
                break;
            case "3":
                Main.setImagesFolder();
                _fileManager.setPhotosLocation();
                break;
            case "4":
                System.out.print("New password: ");
                Persistence.save(in.nextLine());
                break;
        }
        Persistence.save();
    }
}
