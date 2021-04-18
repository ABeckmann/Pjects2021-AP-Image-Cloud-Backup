package com.company;

/**
 * @author Alex Pearce 913987
 * Stores the image data, this replaces what is often a Tuple in different languages
 */
public class Image {
    public byte[] hash;
    public String name;

    /**
     * Constructor
     * @param hash
     * @param name
     */
    public Image(byte[] hash, String name) {
        this.hash = hash;
        this.name = name;
    }
}
