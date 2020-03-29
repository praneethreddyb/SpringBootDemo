package com.demo.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SprinngBootStarter extends SpringBootServletInitializer{

	private static final Logger log = LogManager.getLogger(SprinngBootStarter.class);
	
	public static void main(String[] args) {
		log.info("Starting");
        SpringApplication.run(SprinngBootStarter.class, args);
    }
}
