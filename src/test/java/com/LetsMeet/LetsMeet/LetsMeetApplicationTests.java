package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;

import com.LetsMeet.LetsMeet.TestingTools.TestingUsers;
import com.LetsMeet.Models.UserData;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class  LetsMeetApplicationTests {

	@Autowired
	private APIHandler controller;

	private static ArrayList<TestingUsers> testUsers = new ArrayList<>();


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

		TestingUsers user = new TestingUsers(fName, lName, email, password);
		testUsers.add(user);

		// Check correct message has been returned
		assertEquals("User Account Created Successfully", result);

		// Double check by searching DB
		UserModel userModel = new UserModel();
		boolean present = userModel.checkEmailExists(email);
		userModel.closeCon();
		assertEquals(true, present);
	}

	@Test
	@Order(3)
	public void checkDBUserRecord(){
		TestingUsers user = testUsers.get(0);
		UserModel model = new UserModel();
		UserData userData = model.getUserByEmail(user.email);
		model.closeCon();

		assertEquals(user.fName, userData.getfName());
		assertEquals(user.lName, userData.getlName());
		assertEquals(user.email, userData.getEmail());

		user.UUID = userData.getUserUUID();
		user.passwordHash = userData.getPasswordHash();
		user.salt = userData.getSalt();
	}

	@Test
	@Order(4)
	public void login(){
		// Test that a user can login
		TestingUsers user = testUsers.get(0);
		String token = this.controller.API_Login(user.email, user.password);

		// Check that the token is in the DB
		UserDBChecker model = new UserDBChecker();
		boolean result = model.checkForToken(token, user.UUID);
		model.closeCon();
		assertEquals(true, result);
		user.token = token;
	}

	@Test
	@Order(5)
	public void userDataReturn(){
		// Test that user data is being returned in the correct format
		TestingUsers user = testUsers.get(0);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String result = mapper.writeValueAsString(this.controller.API_GetUser(user.token));
			String expectedResult = String.format("{\"fName\":\"%s\",\"lName\":\"%s\",\"email\":\"%s\"}", user.fName,
					user.lName, user.email);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : userDataReturn");
			System.out.println(e);
		}
	}

	@Test
	@Order(6)
	public void LoginWithWrongDetails(){
		// Test login with incorrect email
		TestingUsers user = testUsers.get(0);
		String result = this.controller.API_Login(user.email + "Wrong", user.password);
		System.out.println(result);
		assertEquals("Incorrect Details", result);

		// Test login with incorrect password
		result = this.controller.API_Login(user.email, user.password + "Wrong");
		System.out.println(result);
		assertEquals("Incorrect Details", result);
	}

	@Test
	@Order(7)
	public void CheckToken(){
		TestingUsers user = testUsers.get(0);

		// Test incorrect token
		Object[] response = UserManager.verifyAPItoken(user.token + "Wrong");
		boolean result = (boolean) response[0];
		assertEquals(false, result);

		// Test correct token
		response = UserManager.verifyAPItoken(user.token);
		result = (boolean) response[0];
		assertEquals(true, result);
	}

	@Test
	@Order(8)
	public void deleteUser(){
		// Test deleting a user
		TestingUsers user = testUsers.get(0);
		String result = this.controller.API_DeleteUser(user.token);
		assertEquals("User Deleted", result);
	}

	// Test API Home
	@Test
	@Order(9)
	public void Home(){
		String result = this.controller.API_Home();
		System.out.println(result);
		assertEquals(false, true);
	}

//
//	@Test
//	@Order(6)
//	public void createEvent(){
//		// Test creating event
//	}
//
//	@Test
//	@Order(7)
//	public void deleteEvent(){
//		// Test deleting event
//	}
//
//	@Test
//	@Order(8)
//	public void joinEvent(){
//		// Test a user joining an event
//	}
//
//	@Test
//	@Order(9)
//	public void ownerDeleteEventWithParticipant(){
//		// Test owner deleting event with a participant already joined
//	}
//
//	@Test
//	@Order(10)
//	public void ParticipantLeaveEvent(){
//		// Test participant leaving an event
//	}
//
//	@Test
//	@Order(11)
//	public void NonOwnerDeletingEvent(){
//		// Test deleting an event while not being the owner
//	}

	@AfterAll
	public static void cleanup(){
		// Remove test records from DB
		// Remove users
		UserDBChecker model = new UserDBChecker();
		for(TestingUsers user : testUsers){
			model.removeUserByEmail(user.email);
		}

		// Remove events

		model.closeCon();
	}
}
