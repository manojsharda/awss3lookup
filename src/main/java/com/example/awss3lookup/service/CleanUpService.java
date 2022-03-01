package com.example.awss3lookup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CleanUpService {
    private Logger logger = LoggerFactory.getLogger(CleanUpService.class);

    public void cleanUp(String path, String fileName) {
        try {
            Files.newDirectoryStream(Paths.get(path)).forEach(file -> {
                if (file.getFileName().toString().equalsIgnoreCase(fileName)) {
                    try {
                        Files.delete(file);
                        logger.info("Directory cleaned. file deleted: " + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new UncheckedIOException(e);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Directory cleanup failed.");
        }
    }
}
