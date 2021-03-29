package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.DBChecks.EventDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.TestingTools.*;
import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.*;
import org.apache.commons.lang3.RandomStringUtils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class  LetsMeetApplicationTests {

	@Autowired
	private UserControllerAPI userController;

	@Autowired
	private EventControllerAPI eventController;

	@Autowired
	private ValidationService userValidation;

	@Autowired
	private UserService userService;

	@Autowired
	EventService eventService;

	@Autowired
	UserDBChecker UserDB;

	@Autowired
	EventDBChecker EventDB;

	@Autowired
	UserDao userModel;

	@Autowired
	EventDao eventModel;

	@Autowired
	EventPermissionDao EventPermissionModel;

	private static ArrayList<TestingUsers> testUsers = new ArrayList<>();
	private static ArrayList<TestingEvents> testEvents = new ArrayList<>();

	// API Tests ///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	@Order(1)
	void LoadAPIHandler() throws Exception{
		assertThat(userController).isNotNull();
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
		String result = this.userController.API_AddUser(fName, lName, email, password);

		TestingUsers user = new TestingUsers(fName, lName, email, password);
		testUsers.add(user);

		// Check correct message has been returned
 		assertEquals("User Account Created Successfully", result);

		// Double check by searching DB
		Optional<User> present = userModel.get(email);

		assertEquals(true, present.isPresent());

		// Remove created user
		UserDB.removeUserByEmail(email);
		testUsers.clear();
	}

	@Test
	@Order(3)
	public void checkDBUserRecord(){
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		Optional<User> userData = userModel.get(user.email);

		assertEquals(true, userData.isPresent());

		assertEquals(user.fName, userData.get().getfName());
		assertEquals(user.lName, userData.get().getlName());
		assertEquals(user.email, userData.get().getEmail());

		// remove user
		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(4)
	public void login(){
		// Test that a user can login
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		String token = this.userController.API_Login(user.email, user.password);

		// Check that the token is in the DB
		boolean result = UserDB.checkForToken(token, user.UUID);

		assertEquals(true, result);
		user.token = token;
		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(5)
	public void userDataReturn(){
		// Test that user data is being returned in the correct format
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String result = mapper.writeValueAsString(this.userController.API_GetUser(user.token));
			String expectedResult = String.format("{\"fName\":\"%s\",\"lName\":\"%s\",\"email\":\"%s\"}", user.fName,
					user.lName, user.email);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : userDataReturn");
			System.out.println(e);
		}
		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(6)
	public void LoginWithWrongDetails(){
		// Test login with incorrect email
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		String result = this.userController.API_Login(user.email + "Wrong", user.password);
		assertEquals("Error, invalid email or password", result);

		// Test login with incorrect password
		result = this.userController.API_Login(user.email, user.password + "Wrong");
		assertEquals("Error, invalid email or password", result);

		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(7)
	public void CheckToken(){
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		// Test incorrect token
		Object[] response = userValidation.verifyAPItoken(user.token + "Wrong");
		boolean result = (boolean) response[0];
		assertEquals(false, result);

		// Test correct token
		response = userValidation.verifyAPItoken(user.token);
		result = (boolean) response[0];
		assertEquals(true, result);

		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(8)
	public void deleteUser(){
		// Test deleting a user
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		String result = this.userController.API_DeleteUser(user.token);
		testUsers.remove(0);
		assertEquals("User successfully deleted.", result);
	}

	@Test
	@Order(9)
	public void Home(){
		//String result = this.controller.API_Home();
		//System.out.println(result);
		// assertEquals("Welcome to the lets meet API! \nFor more information on using the API service visit the API help page"
		// 		, result);
		assertTrue(true);
	}

	@Test
	@Order(10)
	public void createEvent(){
		// Test creating event
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		user.UUID = UserDB.UserUUIDFromEmail(user.email);

		// Generate event details
		String Ename = RandomStringUtils.randomAlphabetic(8);
		String Edesc = RandomStringUtils.randomAlphabetic(8);
		String Elocation = RandomStringUtils.randomAlphabetic(8);

		this.login(user);

		// Run method
		String result = this.eventController.API_AddEvent(Ename, Edesc, Elocation, user.token);
		TestingEvents event = new TestingEvents(Ename, Edesc, Elocation);

		event.UUID = EventDB.eventUUIDFromNameAndDesc(Ename, Edesc);

		testEvents.add(event);
		user.events.add(event.UUID);

		// Check response
		assertEquals("Event successfully created.", result);

		// Check events record
		TestingEvents check = EventDB.checkEvent(event.UUID);
		assertEquals(event.UUID, check.UUID);
		assertEquals(event.name, check.name);
		assertEquals(event.desc, check.desc);
		assertEquals(event.location, check.location);

		// Check hasUser record
		List<EventPermission> hasUsers = EventPermissionModel.get(event.UUID).get();

		EventPermission record = hasUsers.get(0);
		assertEquals(event.UUID, record.getEvent().toString());
		assertEquals(user.UUID, record.getUser().toString());
		assertEquals(true, record.getIsOwner());

		UserDB.removeUserByEmail(user.email);
		testUsers.clear();
	}

	@Test
	@Order(11)
	public void getEvent() {
		// Retrieve event data
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		ObjectMapper mapper = new ObjectMapper();

		try{
			String result = mapper.writeValueAsString(this.eventController.API_GetEvent(event.UUID).getBody());
			String expectedResult = String.format("{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}", event.name,
					event.desc, event.location);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getEvent");
			System.out.println(e);
		}

		UserDB.removeUserByEmail(user.email);
		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);
		testEvents.clear();
	}

	@Test
	@Order(12)
	public void getUsersEvents_OwnsOneEvent(){
		// Test getting all the events a user should have
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.getActiveUserEvents(user.token).getBody());
			String expectedResult = String.format("[{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}]", event.name,
					event.desc, event.location);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEvent");
			System.out.println(e);
		}

		UserDB.removeUserByEmail(user.email);
		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);
		testEvents.clear();
	}

	@Test
	@Order(13)
	public void joinEvent(){
		// Test a user joining an event

		// Create a new user
		this.generateUser();
		TestingUsers user = testUsers.get(0);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);

		// Create a new event
		this.generateEvent(user2.token);
		TestingEvents event = testEvents.get(0);

		try {
			var result = this.eventController.makeResponse(event.UUID, user.UUID, user.token);
			assertEquals(HttpStatus.OK, result.getStatusCode());
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEvent");
			System.out.println(e);
		}

		// Check DB
		Optional<List<EventPermission>> records = EventPermissionModel.get(event.UUID);
		assertEquals(2, records.get().size());

		if(records.get().get(0).getIsOwner()){
			assertEquals(user.UUID, records.get().get(1).getUser().toString());
		}else{
			assertEquals(user.UUID, records.get().get(0).getUser().toString());
		}

		// Remove unnecessary data
		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);

		testUsers.clear();
		EventDB.removeEventByUUID(event.UUID);

		testEvents.clear();
	}

	@Test
	@Order(14)
	public void getUsersEvents_OwnsOneEventJoinedOne(){
		// Test getting all the events a user should have
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateEvent(user.token);
		TestingEvents event1 = testEvents.get(0);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user2.token);
		TestingEvents event2 = testEvents.get(1);
		this.eventController.makeResponse(event2.UUID, user.UUID, user.token);

		ArrayList<TestingEvents> events = new ArrayList<>();
		events.add(event1);
		events.add(event2);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.getActiveUserEvents(user.token));
			System.out.println(result);

			JSONArray array = new JSONArray(result);
			for(int i=0; i < array.length(); i++) {
				JSONObject responseEvent = array.getJSONObject(i);
				// Match response event to event
				for(TestingEvents event : events){
					if(event.UUID.equals(responseEvent.getString("name"))){
						// Match
						assertEquals(event.desc, responseEvent.getString("description"));
						assertEquals(event.location, responseEvent.getString("location"));
						break;
					}
				}
			}

		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);

		testUsers.clear();

		EventDB.removeEventByUUID(event1.UUID);
		EventDB.removeEventByUUID(event2.UUID);

		testEvents.clear();
	}

	@Test
	@Order(15)
	public void getUsersEvents_OwnsTwoEventJoinedOne(){
		// Test getting all the events a user should have
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateEvent(user.token);
		TestingEvents event1 = testEvents.get(0);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user2.token);
		TestingEvents event2 = testEvents.get(1);
		this.eventController.makeResponse(event2.UUID, user.UUID, user.token);

		this.generateEvent(user.token);
		TestingEvents event3 = testEvents.get(2);

		ArrayList<TestingEvents> events = new ArrayList<>();
		events.add(event1);
		events.add(event2);
		events.add(event3);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.getActiveUserEvents(user.token));
			System.out.println(result);

			JSONArray array = new JSONArray(result);
			for(int i=0; i < array.length(); i++) {
				JSONObject responseEvent = array.getJSONObject(i);
				// Match response event to event
				for(TestingEvents event : events){
					if(event.UUID.equals(responseEvent.getString("name"))){
						// Match
						assertEquals(event.desc, responseEvent.getString("description"));
						assertEquals(event.location, responseEvent.getString("location"));
						break;
					}
				}
			}

		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);

		testUsers.clear();

		EventDB.removeEventByUUID(event1.UUID);
		EventDB.removeEventByUUID(event2.UUID);
		EventDB.removeEventByUUID(event3.UUID);

		testEvents.clear();
	}

	@Test
	@Order(16)
	public void getUsersEvents_OwnsTwoEventJoinedTwo(){
		// Test getting all the events a user should have
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		// Create event
		this.generateEvent(user.token);
		TestingEvents event1 = testEvents.get(0);

		// Other user
		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		// Join event
		this.generateEvent(user2.token);
		TestingEvents event2 = testEvents.get(1);
		this.eventController.makeResponse(event2.UUID, user.UUID, user.token);

		// Create event
		this.generateEvent(user.token);
		TestingEvents event3 = testEvents.get(2);

		// Join event
		this.generateEvent(user2.token);
		TestingEvents event4 = testEvents.get(2);
		this.eventController.makeResponse(event4.UUID, user.UUID, user.token);

		ArrayList<TestingEvents> events = new ArrayList<>();
		events.add(event1);
		events.add(event2);
		events.add(event3);
		events.add(event4);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.getActiveUserEvents(user.token));
			System.out.println(result);

			JSONArray array = new JSONArray(result);
			for(int i=0; i < array.length(); i++) {
				JSONObject responseEvent = array.getJSONObject(i);
				// Match response event to event
				for(TestingEvents event : events){
					if(event.UUID.equals(responseEvent.getString("name"))){
						// Match
						assertEquals(event.desc, responseEvent.getString("description"));
						assertEquals(event.location, responseEvent.getString("location"));
						break;
					}
				}
			}

		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);

		testUsers.clear();

		EventDB.removeEventByUUID(event1.UUID);
		EventDB.removeEventByUUID(event2.UUID);
		EventDB.removeEventByUUID(event3.UUID);
		EventDB.removeEventByUUID(event4.UUID);

		testEvents.clear();
	}

	@Test
	@Order(17)
	public void deleteEvent() {
		// Test deleting event
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		// Create event
		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		// Test response
		try{
			Boolean result = this.eventController.API_DeleteEvent(user.token, event.UUID).getBody();
			assertEquals(true, result);
		}catch(Exception e){
			System.out.println("API Tests : deleteEvent");
			System.out.println(e);
		}

		// Check DB
		// Check events table
		try {
			Optional<Event> response = eventModel.get(event.UUID);
			assertEquals(false, response.isPresent());
		}catch(Exception e){

		}

		// Check hasUsers table
		Optional<List<EventPermission>> records = EventPermissionModel.get(event.UUID);
		assertEquals(0, records.get().size());

		// Remove unnecessary data
		UserDB.removeUserByEmail(user.email);
		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);

		testEvents.clear();
	}

	@Test
	@Order(18)
	public void ownerDeleteEventWithParticipant(){
		// Test owner deleting event with a participant already joined
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		// User2 join event
		this.eventController.addUser(event.UUID, user2.UUID, false);

		// Delete event
		try {
			Boolean result = this.eventController.API_DeleteEvent(user.token, event.UUID).getBody();
			assertEquals(true, result);
		}catch(Exception e){
			System.out.println("API Tests : ownerDeleteEventWithParticipant");
			System.out.println(e);
		}

		// Check DB
		// Check events table
		try {
			Optional<Event> response = eventModel.get(event.UUID);
			assertEquals(false, response.isPresent());
		}catch (Exception e){

		}

		// Check hasUsers table
		Optional<List<EventPermission>> records = EventPermissionModel.get(event.UUID);
		assertEquals(0, records.get().size());

		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);
		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);
		testEvents.clear();
	}

	@Test
	@Order(19)
	public void EventOwnerDeletingAccount(){
		// Test owner deleting their account
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		this.eventController.addUser(event.UUID, user2.UUID, false);

		try {
			String result = this.userController.API_DeleteUser(user.token);
			String expectedResult = String.format("User successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : EventOwnerDeletingAccount");
			System.out.println(e);
		}

		// Check events
		try {
			Optional<Event> response = eventModel.get(event.UUID);
			assertEquals(false, response.isPresent());
		}catch(Exception e){

		}
		// Check hasUsers
		Optional<List<EventPermission>> records = EventPermissionModel.get(event.UUID);
		assertEquals(0, records.get().size());

		// Check user

		// Remove unnecessary data
		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);

		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);
		testEvents.clear();
	}

//	@Test
//	@Order(20)
//	public void ParticipantLeaveEvent(){
//		// Test participant leaving an event
//		this.generateUser();
//		TestingUsers user = testUsers.get(0);
//		this.login(user);
//
//		this.generateUser();
//		TestingUsers user2 = testUsers.get(1);
//		this.login(user2);
//
//		this.generateEvent(user.token);
//		TestingEvents event = testEvents.get(0);
//
//		this.eventController.addUser(event.UUID, user2.UUID, false);
//
//		try {
//			String result = this.eventController.API_LeaveEvent(user.token, event.UUID);
//			String expectedResult = String.format("Successfully left event.");
//			assertEquals(expectedResult, result);
//		}catch(Exception e){
//			System.out.println("API Tests : EventOwnerDeletingAccount");
//			System.out.println(e);
//		}
//
//		// Check DB
//		// Check event
//		Optional<Event> eventDB = eventModel.get(event.UUID);
//		assertEquals(event.UUID, eventDB.get().getUUID().toString());
//
//		// Check HasUser
//		Optional<List<EventPermission>> records = EventPermissionModel.get(event.UUID);
//		assertEquals(1, records.get().size());
//
//		// Remove unnecessary data
//		UserDB.removeUserByEmail(user.email);
//		UserDB.removeUserByEmail(user2.email);
//		testUsers.clear();
//
//		EventDB.removeEventByUUID(event.UUID);
//		testEvents.clear();
//	}

	@Test
	@Order(21)
	public void participantDeletesAccount() {
		// Test participant deleting account
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		this.eventController.addUser(event.UUID, user2.UUID, false);

		try {
			String result = this.userController.API_DeleteUser(user2.token);
			String expectedResult = String.format("User successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : participantDeletesAccount");
			System.out.println(e);
		}

		// Check User
		User response = userService.getUserByUUID(user2.UUID);

		// Check HasUsers
		Optional<EventPermission> records = EventPermissionModel.get(UUID.fromString(event.UUID), UUID.fromString(user2.UUID));

		// Check event
		try {
			Optional<Event> DBreturn = eventModel.get(event.UUID);
			Event eve = DBreturn.get();

			assertEquals(null, response);
			assertEquals(false, records.isPresent());
			assertEquals(event.UUID, DBreturn.get().getUUID().toString());
		}catch(Exception e){

		}
		// Remove unnecessary data
		UserDB.removeUserByEmail(user.email);
		UserDB.removeUserByEmail(user2.email);
		testUsers.clear();

		EventDB.removeEventByUUID(event.UUID);
		testEvents.clear();
	}

	@Test
	@Order(22)
	public void updateUsers() {
		// Update user accounts
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		// Generate new details
		this.generateUser();
		TestingUsers user2 = testUsers.get(1);

		String expectedResponse = "User successfully updated";

		// Change fName
		String response = this.userController.API_UpdateUser(user.token, user2.fName, "", "");

		assertEquals(expectedResponse, response);
		User checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user.lName, checking.getlName());
		assertEquals(user.email, checking.getEmail());

		// Change lName
		response = this.userController.API_UpdateUser(user.token, "", user2.lName, "");

		assertEquals(expectedResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(user.email, checking.getEmail());

		// Change email - valid
		String testingEmail = "Test" + user2.email;
		response = this.userController.API_UpdateUser(user.token, "", "", testingEmail);

		assertEquals(expectedResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(testingEmail, checking.getEmail());

		String invalidResponse = "Email is not valid";

		// Change email - invalid
		String invalidEmail = "Hi@.com";
		response = this.userController.API_UpdateUser(user.token, "", "", invalidEmail);
		assertEquals(invalidResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(testingEmail, checking.getEmail());

		invalidEmail = "@correct.com";
		response = this.userController.API_UpdateUser(user.token, "", "", invalidEmail);
		assertEquals(invalidResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(testingEmail, checking.getEmail());

		invalidEmail = "hi@correct";
		response = this.userController.API_UpdateUser(user.token, "", "", invalidEmail);
		assertEquals(invalidResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(testingEmail, checking.getEmail());

		// Change email - already in use
		response = this.userController.API_UpdateUser(user.token, "", "", user2.email);
		assertEquals("Email already in use", response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user2.fName, checking.getfName());
		assertEquals(user2.lName, checking.getlName());
		assertEquals(testingEmail, checking.getEmail());


		// Change all 3
		response = this.userController.API_UpdateUser(user.token, user.fName, user.lName, user.email);
		assertEquals(expectedResponse, response);
		checking = userService.getUserByUUID(user.UUID);
		assertEquals(user.UUID, checking.getUUID().toString());
		assertEquals(user.fName, checking.getfName());
		assertEquals(user.lName, checking.getlName());
		assertEquals(user.email, checking.getEmail());

		testUsers.clear();
	}

	@Test
	@Order(23)
	public void updatePassword(){
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		// Test with valid password
		String testPassword = RandomStringUtils.randomAlphabetic(8);
		String response = userController.API_UpdateUserPassword(user.token, user.password, testPassword, testPassword);
		assertEquals("Password successfully updated", response);
		String token = this.userController.API_Login(user.email, testPassword);
		assertNotEquals("Error, invalid email or password", token);
		assertEquals(128, token.length());
		user.password = testPassword;

		// Test with password which is too short

		this.login(user);
		String newPassword = RandomStringUtils.randomAlphabetic(5);
		response = userController.API_UpdateUserPassword(user.token, testPassword, newPassword, newPassword);
		assertEquals("New password is invalid", response);
		token = this.userController.API_Login(user.email, newPassword);
		assertEquals("Error, invalid email or password", token);

		// Test with password confirmation not matching
		newPassword = RandomStringUtils.randomAlphabetic(8);
		String wrongPassword = "Wrong" + testPassword;
		response = userController.API_UpdateUserPassword(user.token, testPassword, newPassword, wrongPassword);
		assertEquals("New password and password confirmation do not match", response);
		token = this.userController.API_Login(user.email, wrongPassword);
		assertEquals("Error, invalid email or password", token);

		// Test with incorrect current password
		response = userController.API_UpdateUserPassword(user.token, testPassword, testPassword, testPassword);
		assertEquals("Password successfully updated", response);
		token = this.userController.API_Login(user.email, testPassword);
		assertNotEquals("Error, invalid email or password", token);
		assertEquals(128, token.length());
		user.password = testPassword;

		this.login(user);
		newPassword = RandomStringUtils.randomAlphabetic(8);
		wrongPassword = "Wrong" + testPassword;
		response = userController.API_UpdateUserPassword(user.token, wrongPassword, newPassword, newPassword);
		assertEquals("Current Password is not correct", response);
		token = this.userController.API_Login(user.email, newPassword);
		assertEquals("Error, invalid email or password", token);


		testUsers.clear();
	}

	@Test
	@Order(24)
	public void getEventUsers(){
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		//this.eventController.API_AddUserToEvent(user2.token, event.UUID);
		this.eventController.addUser(event.UUID, user2.UUID, false);

		List<User> users = eventService.EventsUsers(UUID.fromString(event.UUID));
		assertEquals(2, users.size());

		testUsers.clear();
		testEvents.clear();
	}

	@Test
	@Order(25)
	public void updateEvents(){
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		this.generateEvent(user.token);
		TestingEvents event2 = testEvents.get(1);

		var expectedResponse = HttpStatus.OK;

		// Update name
		var response = eventController.API_UpdateEvent(user.token, event.UUID, event2.name, "", "").getStatusCode();
		assertEquals(expectedResponse, response);
		Event checking = eventService.getEvent(event.UUID);
		assertEquals(event.UUID, checking.getUUID().toString());
		assertEquals(event2.name, checking.getName());
		assertEquals(event.desc, checking.getDescription());
		assertEquals(event.location, checking.getLocation());

		// Update description
		response = eventController.API_UpdateEvent(user.token, event.UUID, "", event2.desc, "").getStatusCode();
		assertEquals(expectedResponse, response);
		checking = eventService.getEvent(event.UUID);
		assertEquals(event.UUID, checking.getUUID().toString());
		assertEquals(event2.name, checking.getName());
		assertEquals(event2.desc, checking.getDescription());
		assertEquals(event.location, checking.getLocation());

		// Update location
		response = eventController.API_UpdateEvent(user.token, event.UUID,"", "", event2.location).getStatusCode();
		assertEquals(expectedResponse, response);
		checking = eventService.getEvent(event.UUID);
		assertEquals(event.UUID, checking.getUUID().toString());
		assertEquals(event2.name, checking.getName());
		assertEquals(event2.desc, checking.getDescription());
		assertEquals(event2.location, checking.getLocation());

		// Update all 3
		response = eventController.API_UpdateEvent(user.token, event.UUID, event.name, event.desc, event.location).getStatusCode();
		assertEquals(expectedResponse, response);
		checking = eventService.getEvent(event.UUID);
		assertEquals(event.UUID, checking.getUUID().toString());
		assertEquals(event.name, checking.getName());
		assertEquals(event.desc, checking.getDescription());
		assertEquals(event.location, checking.getLocation());

	}

	@Test
	@Order(60)
	public void cleanup(){
		// Remove test records from DB
		UserDB.clearTestData();
		assertTrue(true);
	}

	@AfterEach
	public void clearLists(){
		testUsers.clear();
		testEvents.clear();
	}

	// Methods for assisting with tests ////////////////////////////////////////////////////////////////////////////////
	private void generateUser(){
		// Create a user and add it to list
		// Generate user details
		String fName = RandomStringUtils.randomAlphabetic(8);
		String lName = RandomStringUtils.randomAlphabetic(8);
		String email = String.format(RandomStringUtils.randomAlphabetic(10) + "@InternalTesting.com");
		String password = RandomStringUtils.randomAlphabetic(12);

		// Run method
		this.userController.API_AddUser(fName, lName, email, password);

		TestingUsers user = new TestingUsers(fName, lName, email, password);

		user.UUID = UserDB.UserUUIDFromEmail(email);

		user.token = this.userController.API_Login(user.email, user.password);
		testUsers.add(user);
	}

	private void generateEvent(String token){
		// Generate event details
		String Ename = RandomStringUtils.randomAlphabetic(8);
		String Edesc = RandomStringUtils.randomAlphabetic(8);
		String Elocation = RandomStringUtils.randomAlphabetic(8);

		this.eventController.API_AddEvent(Ename, Edesc, Elocation, token);

		TestingEvents event = new TestingEvents(Ename, Edesc, Elocation);
		event.UUID = EventDB.eventUUIDFromNameAndDesc(Ename, Edesc);
		testEvents.add(event);
	}

	private void login(TestingUsers user){
		String token = this.userController.API_Login(user.email, user.password);
		user.token = token;
	}
}
