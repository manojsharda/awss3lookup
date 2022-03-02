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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppTestConfig.class, ExtractZipFileService.class, CleanUpService.class})
class ExtractZipFileServiceTest {
    @Autowired
    private ExtractZipFileService extractZipFileService;
    @Autowired
    private CleanUpService cleanUpService;
    @Value("${application.source.file.path}")
    private String downloadDir;
    @Value("${application.csv.file.path}")
    private String csvFilesDir;
    @Value("${application.s3bucket.file.path}")
    private String s3bucket;

    @BeforeEach
    void setUp() {
        try {
            FileUtils.copyFileToDirectory(new File(s3bucket + "data.zip"), new File(downloadDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            FileUtils.cleanDirectory(new File(csvFilesDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUnZipFile() {
        extractZipFileService.unZipFile("data.zip");
        File[] files = new File(csvFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));
        assertEquals(3, files.length);
    }
}