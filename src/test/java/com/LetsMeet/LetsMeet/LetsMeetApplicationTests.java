package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.lang3.RandomStringUtils;

import static org.junit.Assert.*;
import com.LetsMeet.LetsMeet.APIHandler;
import com.LetsMeet.Models.UserModel;

import java.util.ArrayList;

@SpringBootTest
class  LetsMeetApplicationTests {

	@Autowired
	private APIHandler controller;

	private static ArrayList<String> emails = new ArrayList<>();

	// API Tests ///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	@Order(1)
	void LoadAPIHandler() throws Exception{
		assertThat(controller).isNotNull();
	}

	@Test
	@Order(2)
	public void createUser(){
		// Generate user details
		String fName = RandomStringUtils.randomAlphabetic(8);
		String lName = RandomStringUtils.randomAlphabetic(8);
		String email = String.format(RandomStringUtils.randomAlphabetic(10) + "@InternalTesting.com");
		String password = RandomStringUtils.randomAlphabetic(12);

		// Run method
		String result = this.controller.API_AddUser(fName, lName, email, password);
		emails.add(email);
		System.out.println(result);

		// Check correct message has been returned
		assertEquals("User Account Created Successfully", result);

		// Double check by searching DB
		UserModel userModel = new UserModel();
		boolean present = userModel.checkEmailExists(email);
		userModel.closeCon();
		System.out.println(present);
		assertEquals(true, present);
	}

	@Test
	@Order(3)
	public void login(){
		// Test that a user can login
	}

	@Test
	@Order(4)
	public void deleteUser(){
		// Test deleting a user
	}

	@Test
	@Order(5)
	public void createEvent(){
		// Test creating event
	}

	@Test
	@Order(6)
	public void deleteEvent(){
		// Test deleting event
	}

	@Test
	@Order(6)
	public void joinEvent(){
		// Test a user joining an event
	}

	@Test
	@Order(7)
	public void ownerDeleteEventWithParticipant(){
		// Test owner deleting event with a participant already joined
	}

	@Test
	@Order(8)
	public void ParticipantLeaveEvent(){
		// Test participant leaving an event
	}

	@Test
	@Order(9)
	public void NonOwnerDeletingEvent(){
		// Test deleting an event while not being the owner
	}

	@Test
	@Order(10)
	public void LoginWithWrongDetails(){
		// Test login with incorrect details

		// Test with email

		// Test with password
	}

	@Test
	@Order(11)
	public void CheckToken(){
		// Test incorrect token
		// Test correct token
	}

	@AfterAll
	public static void cleanup(){
		// Remove test records from DB
		// Remove users
		UserDBChecker model = new UserDBChecker();
		for(String email : emails){
			model.removeUserByEmail(email);
		}
		model.closeCon();
	}
}
