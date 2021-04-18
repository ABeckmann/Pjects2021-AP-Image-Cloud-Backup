package com.company;

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Alex Pearce 913987
 * This class was taken from LAB work from CSC-318 and modified to work with my project since it carries out identical AES encryption/decryption.
 * This handles the encryption and decryption of bytes as well as AES key generation.
 */
public class AES {
    static Cipher cipher;

    /**
     * Constructor creates an AES instance.
     * @throws Exception Required since getInstance can throw an exception although this will never throw since the
     * instance is always "AES" so can't be a type that doesn't exist.
     */
    public AES() throws Exception {
        cipher = Cipher.getInstance("AES");
    }

    /**
     * Encrypts a byte array using the given key.
     * @param plainTextBytes Bytes to be encrypted
     * @param secretKey Encryption key
     * @return Encrypted bytes
     * @throws Exception if the cipher throws an exception
     */
    public byte[] encrypt(byte[] plainTextBytes, SecretKey secretKey) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainTextBytes);
    }

    /**
     * Decrypts a byte array using the given key.
     * @param encryptedBytes Encrypted bytes to decrypt
     * @param secretKey Encryption key
     * @return Decrypted bytes
     * @throws Exception if the cipher throws an exception
     */
    public byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedBytes);


    }

    /**
     * Generates an AES key from a string.
     * @param password String to be converted to a key
     * @return AES key derived from the password
     * @throws Exception if we are unable to convert the string to bytes.
     */
    public SecretKey generateKeyFromPassword(String password) throws Exception{

        //Get byte representation of password.
        byte[] passwordInBytes = (password).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = sha.digest(passwordInBytes);

        //AES keys are only 128bits (16 bytes) so take first 128 bits of digest.
        key = Arrays.copyOf(key, 16);

        return new SecretKeySpec(key, "AES");
    }
}