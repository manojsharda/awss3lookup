package com.example.awss3lookup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ExtractZipFileService {
    private Logger logger = LoggerFactory.getLogger(ExtractZipFileService.class);
    @Autowired
    private CleanUpService cleanUpService;
    @Value("${application.source.file.path}")
    private String downloadDir;
    @Value("${application.csv.file.path}")
    private String csvFilesDir;

    public void unZipFile(String fileName) {
        Path outDir = Paths.get(csvFilesDir);
        byte[] buffer = new byte[2048];
        try (FileInputStream fis = new FileInputStream(downloadDir + fileName);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream stream = new ZipInputStream(bis)) {
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                Path filePath = outDir.resolve(entry.getName());
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                     BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
                    int len;
                    while ((len = stream.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.error("Unzip Zip File: " + fileName + " Failed.");
        } finally {
            logger.info("Deleting Zip File: " + fileName);
            cleanUpService.cleanUp(downloadDir, fileName);
        }
    }
}
