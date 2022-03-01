package com.example.awss3lookup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class AWSS3UploadService {
    private Logger logger = LoggerFactory.getLogger(AWSS3UploadService.class);
    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private CleanUpService cleanUpService;
    @Value("${application.parquet.file.path}")
    private String parquetFilesDir;
    @Value("${application.csv.file.path}")
    private String csvFilesDir;
    @Value("${application.bucket.name}")
    private String bucketName;

    public void uploadFileToS3(String fileName) {
        File file = new File(parquetFilesDir + fileName);
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024;
        try {
            List<PartETag> partETags = new ArrayList<>();
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, fileName);
            InitiateMultipartUploadResult initResponse = amazonS3Client.initiateMultipartUpload(initRequest);
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - filePosition));
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(fileName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);
                UploadPartResult uploadResult = amazonS3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());
                filePosition += partSize;
            }
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    bucketName, fileName, initResponse.getUploadId(), partETags);
            amazonS3Client.completeMultipartUpload(compRequest);
            //cleanup of the respective file.
            cleanUpService.cleanUp(parquetFilesDir, fileName);
            cleanUpService.cleanUp(parquetFilesDir, "." + fileName + ".crc");
            cleanUpService.cleanUp(csvFilesDir, FilenameUtils.removeExtension(fileName) + ".csv");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("File Upload Failed. for file:" + fileName);
        }
    }
}
