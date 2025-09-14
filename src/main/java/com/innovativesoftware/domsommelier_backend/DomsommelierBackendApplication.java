package com.innovativesoftware.domsommelier_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DomsommelierBackendApplication {
	private static final Logger log = LoggerFactory.getLogger(DomsommelierBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DomsommelierBackendApplication.class, args);
	}
}
