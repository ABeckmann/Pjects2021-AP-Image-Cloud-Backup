package com.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

class ImageUploaderTest {

    @Mock
    private Credentials mockCredentials;

    private ImageUploader imageUploaderUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        imageUploaderUnderTest = new ImageUploader("0x6Cfb8fd999D2Ed658aad6D851987090Ac925ad78", mockCredentials);
    }

    @Test
    void testUpload() {
        // Setup
        final ArrayList<File> imagePaths = new ArrayList<>(Arrays.asList(new File("filename.txt")));

        // Run the test
        imageUploaderUnderTest.upload(imagePaths);

        // Verify the results
    }

    @Test
    void testGetImageHash1() throws IOException {
        // Setup
        final File file = new File("filename.txt");
        Files.write(Paths.get("filename.txt"), new byte[8]);
        byte[] expectedResult = new byte[]{-81,85,112,-11,-95,-127,11,122,-9,-116,-81,75,-57,10,102,15,13,-11,30,66,-70,-7,29,77,-27,-78,50,-115,-32,-24,61,-4};

        // Run the test
        final byte[] result = imageUploaderUnderTest.getImageHash(file);

        // Verify the results
        assertArrayEquals(expectedResult, result);
    }


    @Test
    void testSetPrivateKey() {
        // Setup

        // Run the test
        imageUploaderUnderTest.setPrivateKey("214d01ffbd2bf5b9019da691416058730d3840eb77de95134ecacd84c3eada34");

        // Verify the results
        //Verify no exceptions are thrown
    }
}
