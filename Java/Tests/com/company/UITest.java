package com.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UITest {

    @Mock
    private ImageUploader mockUploader;
    @Mock
    private FileManager mockFileManager;

    private UI uiUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        uiUnderTest = new UI(mockUploader, mockFileManager);
    }

    @Test
    void testInput() {
        // Setup
        when(mockFileManager.status()).thenReturn("result");

        // Configure FileManager.getImageList(...).
        final ArrayList<File> files = new ArrayList<>(Arrays.asList(new File("filename.txt")));
        when(mockFileManager.getImageList()).thenReturn(files);

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        when(mockFileManager.getMissingFileNames()).thenReturn(new ArrayList<>(Arrays.asList("value")));

        //Setup console inputs
        System.setIn(new ByteArrayInputStream("refresh\nexport\nimport\nexit".getBytes()));

        // Run the test
        uiUnderTest.input();

        // Verify the results
        verify(mockFileManager).refresh();
        verify(mockFileManager).exportImages();
        verify(mockFileManager).importImages();
    }

    @Test
    void testPrintHelp() {
        // Setup
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        // Run the test
        uiUnderTest.printHelp();

        // Verify the results
        assert(outContent.toString() != "");
    }

    @Test
    void testPrintSetPrivateKey() {
        // Setup

        System.setIn(new ByteArrayInputStream("1\nprivateKey\n".getBytes()));
        // Run the test
        uiUnderTest.printSet();

        // Verify the results
        verify(mockUploader).setPrivateKey("privateKey");

    }

    @Test
    void testPrintSetContractAddress() {
        // Setup

        System.setIn(new ByteArrayInputStream("2\ncontractAddress\n".getBytes()));
        // Run the test
        uiUnderTest.printSet();

        // Verify the results
        verify(mockUploader).set_contract("contractAddress");
    }
}
