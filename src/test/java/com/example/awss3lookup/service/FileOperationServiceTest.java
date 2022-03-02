package com.example.awss3lookup.service;

import com.example.awss3lookup.config.AppTestConfig;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppTestConfig.class, FileOperationService.class, AWSS3UploadService.class})
class FileOperationServiceTest {
    @Autowired
    private FileOperationService fileOperationService;
    @MockBean
    private AWSS3UploadService awsS3UploadService;
    @Value("${application.csv.file.path}")
    private String csvFilesDir;
    @Value("${application.s3bucket.file.path}")
    private String s3bucket;
    @Value("${application.parquet.file.path}")
    private String parquetFilesDir;

    @BeforeEach
    void setUp() {
        File[] files = new File(s3bucket).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));
        Arrays.stream(files).forEach(file -> {
            try {
                FileUtils.copyFileToDirectory(file, new File(csvFilesDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @AfterEach
    void tearDown() {
        try {
            FileUtils.cleanDirectory(new File(csvFilesDir));
            FileUtils.cleanDirectory(new File(parquetFilesDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSearchExtractedFiles() {
        fileOperationService.searchExtractedFiles();
        File[] csvFiles = new File(csvFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));
        File[] parquetFiles = new File(parquetFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".parquet"));
        File[] parquetCRCFiles = new File(parquetFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".parquet.crc"));
        assertEquals(3, csvFiles.length);
        assertEquals(3, parquetFiles.length);
        assertEquals(3, parquetCRCFiles.length);
    }
}