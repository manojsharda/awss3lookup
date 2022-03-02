package com.example.awss3lookup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.example.awss3lookup.config.AppTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppTestConfig.class, AWSS3UploadService.class, CleanUpService.class})
class AWSS3UploadServiceTest {
    @Autowired
    private AWSS3UploadService awsS3UploadService;
    @MockBean
    private AmazonS3 amazonS3Client;
    @Autowired
    private CleanUpService cleanUpService;
    @Value("${application.parquet.file.path}")
    private String parquetFilesDir;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testUploadFileToS3() {
        File file = new File(parquetFilesDir + "testFile.parquet");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InitiateMultipartUploadResult initiateMultipartUploadResult = new InitiateMultipartUploadResult();
        initiateMultipartUploadResult.setUploadId("testUploadId");
        Mockito.when(amazonS3Client.initiateMultipartUpload(any())).thenReturn(initiateMultipartUploadResult);
        awsS3UploadService.uploadFileToS3(file.getName());
        File[] files = new File(parquetFilesDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".parquet"));
        assertEquals(0, files.length);
    }
}