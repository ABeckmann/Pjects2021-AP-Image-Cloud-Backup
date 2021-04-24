package com.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class FileManagerTest {

    @Mock
    private ImageUploader mockUploader;
    private Main mockMain;

    private FileManager fileManagerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        fileManagerUnderTest = new FileManager("location", mockUploader);
    }

    @Test
    void testScanPhotos() throws IOException {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);
        File dir = new File("location\\");
        dir.mkdir();
        Files.write(Paths.get("location\\filename.jpg"), new byte[0]);


        // Run the test
        fileManagerUnderTest.scanPhotos();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("location\\filename.jpg"))));
    }

    @Test
    void testScanPhotos_ImageUploaderGet_uploadedImagesReturnsNoItems() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        fileManagerUnderTest.scanPhotos();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("location\\filename.jpg"))));
    }

    @Test
    void testRefresh() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        // Run the test
        fileManagerUnderTest.refresh();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("location\\filename.jpg"))));
    }

    @Test
    void testStatus() {
        // Setup

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        // Run the test
        final String result = fileManagerUnderTest.status();

        // Verify the results
        assert(result != "");
    }

    @Test
    void testStatus_ImageUploaderReturnsNoItems() {
        // Setup
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());
        String expectedResult = "Number of local images:          1\n" +
                                "Number of uploaded image hashes: 0\n" +
                                "Number of missing local files:   0";

        // Run the test
        final String result = fileManagerUnderTest.status();

        // Verify the results
        assert(result.equals(expectedResult));
    }

    @Test
    void testGetMissingFileNames() {
        // Setup

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        // Run the test
        final ArrayList<String> result = fileManagerUnderTest.getMissingFileNames();

        // Verify the results
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testGetMissingFileNames_ImageUploaderReturnsNoItems() {
        // Setup
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        final ArrayList<String> result = fileManagerUnderTest.getMissingFileNames();

        // Verify the results
        assert(result.size() == 0);
    }

    @Test
    void testGetImageList() {
        // Setup
        final ArrayList<File> expectedResult = new ArrayList<>(Arrays.asList(new File("location\\filename.jpg")));

        // Run the test
        final ArrayList<File> result = fileManagerUnderTest.getImageList();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testExportImages() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // Run the test
        fileManagerUnderTest.exportImages();
    }


    @Test
    void testImportImages() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        when(mockUploader.getImageHash(any(byte[].class))).thenReturn("content".getBytes());
        when(mockUploader.isImageNameUploaded("name")).thenReturn(false);

        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // Run the test
        fileManagerUnderTest.importImages();
    }

    @Test
    void testImportImages_ImageUploaderGet_uploadedImagesReturnsNoItems() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());
        when(mockUploader.getImageHash(any(byte[].class))).thenReturn("content".getBytes());
        when(mockUploader.isImageNameUploaded("name")).thenReturn(false);

        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // Run the test
        fileManagerUnderTest.importImages();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("location\\filename.jpg"))));
    }

    @Test
    void testSearchImageByName() {
        // Setup
        final File expectedResult = null;

        // Run the test
        final File result = fileManagerUnderTest.searchImageByName("name");

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
