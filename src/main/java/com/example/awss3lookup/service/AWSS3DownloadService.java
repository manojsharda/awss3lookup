package com.example.awss3lookup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class AWSS3DownloadService {
    private Logger logger = LoggerFactory.getLogger(AWSS3DownloadService.class);
    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private ExtractZipFileService extractZipFileService;
    @Autowired
    private FileOperationService fileOperationService;
    @Value("${application.bucket.name}")
    private String bucketName;
    @Value("${application.source.file.path}")
    private String downloadDir;

    @Scheduled(fixedDelay = 60000)
    public void downloadFileFromS3Bucket() {
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            logger.info("S3 Bucket FileKeyName: " + os.getKey());
            S3Object fullObject = amazonS3Client.getObject(new GetObjectRequest(bucketName, os.getKey()));
            if (os.getKey().endsWith(".zip")) {
                try {
                    String filePathName = downloadDir + os.getKey();
                    FileUtils.copyInputStreamToFile(fullObject.getObjectContent(), new File(filePathName));
                    extractZipFileService.unZipFile(os.getKey());
                    fileOperationService.searchExtractedFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
