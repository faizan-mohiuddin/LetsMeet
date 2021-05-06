package com.LetsMeet.LetsMeet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class LetsMeetApplication implements CommandLineRunner{

	// Get logger
	private static final Logger LOGGER=LoggerFactory.getLogger(LetsMeetApplication.class);

	// Load configuration
	@Autowired
	LetsMeetConfiguration config;

	public static void main(String[] args) {

		SpringApplication.run(LetsMeetApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		LOGGER.info("Using data folder: {}", config.getdataFolder());
		LOGGER.info("Using database configuration: {} @ {} with user {}", config.getDatabaseName(),config.getDatabaseHost(),config.getDatabaseUser());
		LOGGER.info("Hosting Application on: {}", config.getApplicationHost());
	}

}
