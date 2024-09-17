package com.innovativesoftware.domsommelier_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class DomsommelierBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(DomsommelierBackendApplication.class, args);
	}
}
