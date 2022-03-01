package com.example.awss3lookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Awss3lookupApplication {
	public static void main(String[] args) {
		SpringApplication.run(Awss3lookupApplication.class, args);
	}
}
