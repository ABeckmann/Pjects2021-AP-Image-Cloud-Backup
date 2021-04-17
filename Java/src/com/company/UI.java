package com.company;

import java.util.Scanner;

public class UI {
    private ImageUploader _uploader;
    private FileManager _fileManager;
    private Persistence _persistance;

    public UI(ImageUploader uploader, FileManager fileManager, Persistence persistance) {
        _uploader = uploader;
        _fileManager = fileManager;
        _persistance = persistance;
    }

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

    public void printHelp() {
        System.out.println();
        System.out.println("Commands:");
        System.out.println(" \"refresh\":  Scans the local images and checks for corruptions and adds new images to the blockchain");
        System.out.println(" \"export <file location>\": Encrypt and export the images to the specified file. This acts as a secure backup for the local images.");
        System.out.println(" \"import <file location>\": Decrypts and verifies the images based on the blockchain. Use this to recover deleted or corrupted images");
        System.out.println(" \"set\": Opens submenu to set the following parameters: (Private key, contract address, images folder)");
        System.out.println(" \"exit\": Closes the program");
    }

    public void printSet() {
        //Clear console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        Scanner in = new Scanner(System.in);

        System.out.println("1: Private key: ");
        System.out.println("2: contract Address: ");
        System.out.println("3: Images Folder: ");
        System.out.print("Option: ");

        String input = in.nextLine().toLowerCase();

        switch (input) {
            case "1":
                System.out.println("Warning: all current local non-blockchain data will be lost, this will wipe your current private key");
                System.out.print("Enter new Private key: ");
                input = in.nextLine();

                //Todo: wipe local storage and reset.
                System.exit(0);

                break;
            case "2":
                System.out.print("Enter new contract address: ");
                input = in.nextLine();
                _uploader.setContract(input);
                break;
            case "3":
                System.out.print("Enter new folder: ");
                input = in.nextLine();
                _fileManager.setPhotosLocation(input);
                break;
        }
    }


    public void setUploader(ImageUploader uploader) {
        _uploader = uploader;
    }

    public void setFileMan(FileManager fileMan) {
        _fileManager = fileMan;
    }

    public void setPersistance(Persistence persistance) {
        _persistance = persistance;
    }

}
