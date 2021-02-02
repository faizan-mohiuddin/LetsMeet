package com.LetsMeet.LetsMeet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class LetsMeetApplication implements CommandLineRunner{

	// Load configuration
	@Autowired
	LetsMeetConfiguration config;

	public static void main(String[] args) {

		SpringApplication.run(LetsMeetApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		System.out.println("Using configuration:");
		System.out.println(config.getDatabaseHost());
		System.out.println(config.getDatabaseName());
		System.out.println(config.getDatabaseUser());
		System.out.println(config.getDatabasePassword());
	}

}
