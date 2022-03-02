package com.example.awss3lookup.service;

import com.example.awss3lookup.config.AppTestConfig;
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
@SpringBootTest(classes = {AppTestConfig.class, CleanUpService.class})
class CleanUpServiceTest {
    @Autowired
    private CleanUpService cleanUpService;
    @Value("${application.cleanup.file.path}")
    private String cleanupTestDir;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCleanUp() {
        File file = new File(cleanupTestDir + "testFile.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cleanUpService.cleanUp(cleanupTestDir, "testFile.txt");
        File[] files = new File(cleanupTestDir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".txt"));
        assertEquals(0, files.length);
    }
}