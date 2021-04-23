package com.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class FileManagerTest {

    @Mock
    private ImageUploader mockUploader;

    private FileManager fileManagerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        fileManagerUnderTest = new FileManager("location", mockUploader);
    }

    @Test
    void testScanPhotos() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());

        // Configure ImageUploader.get_uploadedImages(...).
        final ArrayList<Image> images = new ArrayList<>(Arrays.asList(new Image("content".getBytes(), "name")));
        when(mockUploader.get_uploadedImages()).thenReturn(images);

        // Run the test
        fileManagerUnderTest.scanPhotos();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
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
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
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
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
    }

    @Test
    void testRefresh_ImageUploaderGet_uploadedImagesReturnsNoItems() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        fileManagerUnderTest.refresh();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
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
        assertEquals("result", result);
    }

    @Test
    void testStatus_ImageUploaderReturnsNoItems() {
        // Setup
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        final String result = fileManagerUnderTest.status();

        // Verify the results
        assertEquals("result", result);
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
        assertEquals(new ArrayList<>(Arrays.asList("value")), result);
    }

    @Test
    void testGetMissingFileNames_ImageUploaderReturnsNoItems() {
        // Setup
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        final ArrayList<String> result = fileManagerUnderTest.getMissingFileNames();

        // Verify the results
        assertEquals(new ArrayList<>(Arrays.asList("value")), result);
    }

    @Test
    void testGetImageList() {
        // Setup
        final ArrayList<File> expectedResult = new ArrayList<>(Arrays.asList(new File("filename.txt")));

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

        // Run the test
        fileManagerUnderTest.exportImages();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
    }

    @Test
    void testExportImages_ImageUploaderGet_uploadedImagesReturnsNoItems() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());

        // Run the test
        fileManagerUnderTest.exportImages();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
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

        // Run the test
        fileManagerUnderTest.importImages();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
    }

    @Test
    void testImportImages_ImageUploaderGet_uploadedImagesReturnsNoItems() {
        // Setup
        when(mockUploader.isImageHashAlreadyUploaded(any(byte[].class))).thenReturn(false);
        when(mockUploader.getImageHash(new File("filename.txt"))).thenReturn("content".getBytes());
        when(mockUploader.get_uploadedImages()).thenReturn(new ArrayList<>());
        when(mockUploader.getImageHash(any(byte[].class))).thenReturn("content".getBytes());
        when(mockUploader.isImageNameUploaded("name")).thenReturn(false);

        // Run the test
        fileManagerUnderTest.importImages();

        // Verify the results
        verify(mockUploader).upload(new ArrayList<>(Arrays.asList(new File("filename.txt"))));
    }

    @Test
    void testSearchImageByName() {
        // Setup
        final File expectedResult = new File("filename.txt");

        // Run the test
        final File result = fileManagerUnderTest.searchImageByName("name");

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
