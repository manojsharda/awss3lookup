package com.example.awss3lookup;

import com.example.awss3lookup.config.AppTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
@SpringBootTest(classes={AppTestConfig.class})
class Awss3lookupApplicationTests {

	@Test
	void contextLoads() {
	}

}
