package com.demo.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SprinngBootStarter {

	private static final Logger log = LogManager.getLogger(SprinngBootStarter.class);
	
	public static void main(String[] args) {
		log.info("Starting");
        SpringApplication.run(SprinngBootStarter.class, args);
    }
}
