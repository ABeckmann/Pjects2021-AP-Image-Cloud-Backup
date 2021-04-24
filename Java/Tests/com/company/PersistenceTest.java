package com.company;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceTest {

    @Test
    void testSaveFileExists() {
        assertTrue(Persistence.saveFileExists());
    }

    @Test
    void testSave1() {
        // Setup

        // Run the test
        Persistence.save("password");

        // Verify the results
    }

    @Test
    void testSave2() {
        // Setup

        // Run the test
        Persistence.save();

        // Verify the results
    }
}
