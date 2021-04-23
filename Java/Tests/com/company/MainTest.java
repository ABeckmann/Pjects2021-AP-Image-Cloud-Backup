package com.company;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void testSetImagesFolder1() {
        // Setup
        System.setIn(new ByteArrayInputStream("Images".getBytes()));

        // Run the test
        Main.setImagesFolder();

        // Verify the results
    }

    @Test
    void testGetCredentialsFromPrivateKey() {
        // Setup
        String privateKey = "214d01ffbd2bf5b9019da691416058730d3840eb77de95134ecacd84c3eada34";
        final Credentials expectedResult = Credentials.create(privateKey);
        Main.setPrivateKey(privateKey);

        // Run the test
        final Credentials result = Main.getCredentialsFromPrivateKey();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetPrivateKey() {
        Main.setPrivateKey("result");
        assertEquals("result", Main.getPrivateKey());
    }

    @Test
    void testSetPrivateKey() {
        // Setup

        // Run the test
        Main.setPrivateKey("privateKey");

        // Verify the results
    }

    @Test
    void testGetContractAddress() {
        Main.setContractAddress("result");
        assertEquals("result", Main.getContractAddress());
    }

    @Test
    void testSetContractAddress() {
        // Setup

        // Run the test
        Main.setContractAddress("contractAddress");

        // Verify the results
    }

    @Test
    void testGetImagesFolder() {
        Main.setImagesFolder("result");
        assertEquals("result", Main.getImagesFolder());
    }

    @Test
    void testSetImagesFolder2() {
        // Setup

        // Run the test
        Main.setImagesFolder("imagesFolder");

        // Verify the results
    }
}
