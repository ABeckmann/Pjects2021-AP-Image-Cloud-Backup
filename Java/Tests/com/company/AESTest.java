package com.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AESTest {

    private AES aesUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        aesUnderTest = new AES();
    }

    @Test
    void testEncrypt() throws Exception {
        // Setup
        byte[] key = new byte[16];
        final SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] expected = {4, -58, 98, 32, -68, 88, 109, 122, -94, -84, 67, 68, -44, -118, -25, 109};

        // Run the test
        final byte[] result = aesUnderTest.encrypt("content".getBytes(), secretKey);

        // Verify the results
        assertArrayEquals(expected, result);
    }

    @Test
    void testEncrypt_ThrowsException() {
        // Setup
        final SecretKey secretKey = null;

        // Run the test
        assertThrows(Exception.class, () -> aesUnderTest.encrypt("content".getBytes(), secretKey));
    }

    @Test
    void testDecrypt() throws Exception {
        // Setup
        byte[] key = new byte[16];
        final SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] cipherText = {4, -58, 98, 32, -68, 88, 109, 122, -94, -84, 67, 68, -44, -118, -25, 109};

        // Run the test
        final byte[] result = aesUnderTest.decrypt(cipherText, secretKey);

        // Verify the results
        assertArrayEquals("content".getBytes(), result);
    }

    @Test
    void testDecrypt_ThrowsException() {
        // Setup
        final SecretKey secretKey = null;

        // Run the test
        assertThrows(Exception.class, () -> aesUnderTest.decrypt("content".getBytes(), secretKey));
    }

    @Test
    void testGenerateKeyFromPassword() throws Exception {
        // Setup

        // Run the test
        final SecretKey result = aesUnderTest.generateKeyFromPassword("password");

        // Verify the results
    }
}
