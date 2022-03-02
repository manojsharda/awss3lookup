package com.example.awss3lookup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppTestConfig.class, AWSS3DownloadService.class})
class AWSS3DownloadServiceTest {
    @Autowired
    private AWSS3DownloadService awsS3DownloadService;
    @MockBean
    private AmazonS3 amazonS3Client;
    @MockBean
    private ExtractZipFileService extractZipFileService;
    @MockBean
    private FileOperationService fileOperationService;
    @Value("${application.source.file.path}")
    private String downloadDir;
    @Value("${application.s3bucket.file.path}")
    private String s3bucket;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testDownloadFileFromS3Bucket() {
        ObjectListing objectListing = new ObjectListing();
        objectListing.setBucketName("testBucket");
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey("data.zip");
        objectListing.getObjectSummaries().add(s3ObjectSummary);
        S3Object fullObject = new S3Object();
        fullObject.setBucketName("testBucket");
        fullObject.setKey("data.zip");
        try {
            fullObject.setObjectContent(new S3ObjectInputStream(new FileInputStream(s3bucket + "data.zip"), null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Mockito.when(amazonS3Client.listObjects(anyString())).thenReturn(objectListing);
        Mockito.when(amazonS3Client.getObject(any())).thenReturn(fullObject);
        awsS3DownloadService.downloadFileFromS3Bucket();
        File[] files = new File(downloadDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".zip"));
        assertEquals(1, files.length);
    }
}