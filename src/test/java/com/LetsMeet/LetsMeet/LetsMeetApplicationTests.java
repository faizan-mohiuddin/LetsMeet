package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.DBChecks.EventDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.TestingTools.*;
import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.Models.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.lang3.RandomStringUtils;

import static org.junit.Assert.*;
//import com.LetsMeet.LetsMeet.APIHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	UserDBChecker UserDB;

	@Autowired
	EventDBChecker EventDB;

	@Autowired
	UserDao userModel;

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
		boolean present = userModel.checkEmailExists(email);

		assertEquals(true, present);

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
		assertEquals("User Deleted", result);
	}

	@Test
	@Order(9)
	public void Home(){
		//String result = this.controller.API_Home();
		//System.out.println(result);
		assertEquals(false, true);
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
			String result = mapper.writeValueAsString(this.eventController.API_GetEvent(event.UUID));
			String expectedResult = String.format("{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}", event.name,
					event.desc, event.location);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getEvent");
			System.out.println(e);
		}

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
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
			String result = mapper.writeValueAsString(this.eventController.API_GetMyEvents(user.token));
			String expectedResult = String.format("[{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}]", event.name,
					event.desc, event.location);
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEvent");
			System.out.println(e);
		}

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
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
			String result = this.eventController.API_AddUserToEvent(user.token, event.UUID);
			String expectedResult = String.format("User added to event");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEvent");
			System.out.println(e);
		}

		// Check DB
		EventsModel model = new EventsModel();
		List<HasUsersRecord> records = model.getHasUsers(event.UUID);
		 ;
		assertEquals(2, records.size());

		if(records.get(0).IsOwner){
			assertEquals(user.UUID, records.get(1).UserUUID);
		}else{
			assertEquals(user.UUID, records.get(0).UserUUID);
		}

		// Remove unnecessary data
		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
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
		this.eventController.API_AddUserToEvent(user.token, event2.UUID);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.API_GetMyEvents(user.token));
			String expectedResult = String.format("[{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"},{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}]",
					event1.name, event1.desc, event1.location, event2.name, event2.desc, event2.location);

			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event1.UUID);
		eventModel.removeEventByUUID(event2.UUID);
		 
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
		this.eventController.API_AddUserToEvent(user.token, event2.UUID);

		this.generateEvent(user.token);
		TestingEvents event3 = testEvents.get(2);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.API_GetMyEvents(user.token));
			String expectedResult = String.format("[{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"},{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}" +
							"{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}]",
					event1.name, event1.desc, event1.location, event2.name, event2.desc, event2.location, event3.name, event3.desc, event3.location);

			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event1.UUID);
		eventModel.removeEventByUUID(event2.UUID);
		eventModel.removeEventByUUID(event3.UUID);
		 
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
		this.eventController.API_AddUserToEvent(user.token, event2.UUID);

		// Create event
		this.generateEvent(user.token);
		TestingEvents event3 = testEvents.get(2);

		// Join event
		this.generateEvent(user2.token);
		TestingEvents event4 = testEvents.get(2);
		this.eventController.API_AddUserToEvent(user.token, event4.UUID);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(this.eventController.API_GetMyEvents(user.token));
			String expectedResult = String.format("[{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"},{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}," +
							"{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"},{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}]",
					event1.name, event1.desc, event1.location, event2.name, event2.desc, event2.location, event3.name, event3.desc, event3.location,
					event4.name, event4.desc, event4.location);

			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : getUsersEvents_OwnsOneEventJoinedOne");
			System.out.println(e);
		}

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event1.UUID);
		eventModel.removeEventByUUID(event2.UUID);
		eventModel.removeEventByUUID(event3.UUID);
		eventModel.removeEventByUUID(event4.UUID);
		 
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
			String result = this.eventController.API_DeleteEvent(user.token, event.UUID);
			String expectedResult = String.format("Event successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : deleteEvent");
			System.out.println(e);
		}

		// Check DB
		EventsModel model = new EventsModel();
		// Check events table
		EventData response = model.getEventByUUID(event.UUID);
		assertEquals(null, response);

		// Check hasUsers table
		List<HasUsersRecord> records = model.getHasUsers(event.UUID);
		 ;
		assertEquals(0, records.size());

		// Remove unnecessary data
		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
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
		this.eventController.API_AddUserToEvent(user2.token, event.UUID);

		// Delete event
		try {
			String result = this.eventController.API_DeleteEvent(user.token, event.UUID);
			String expectedResult = String.format("Event successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : ownerDeleteEventWithParticipant");
			System.out.println(e);
		}

		// Check DB
		EventsModel model = new EventsModel();
		// Check events table
		EventData response = model.getEventByUUID(event.UUID);
		assertEquals(null, response);

		// Check hasUsers table
		List<HasUsersRecord> records = model.getHasUsers(event.UUID);
		assertEquals(0, records.size());
		 ;

		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
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

		this.eventController.API_AddUserToEvent(user2.token, event.UUID);

		try {
			String result = this.userController.API_DeleteUser(user.token);
			String expectedResult = String.format("User successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : EventOwnerDeletingAccount");
			System.out.println(e);
		}

		// Check DB
		EventsModel model = new EventsModel();
		UserModel userModel = new UserModel();

		// Check events
		EventData response = model.getEventByUUID(event.UUID);
		assertEquals(null, response);

		// Check hasUsers
		List<HasUsersRecord> records = model.getHasUsers(event.UUID);
		assertEquals(0, records.size());

		// Check user

		// Remove unnecessary data
		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
		testEvents.clear();
	}

	@Test
	@Order(20)
	public void ParticipantLeaveEvent(){
		// Test participant leaving an event
		this.generateUser();
		TestingUsers user = testUsers.get(0);
		this.login(user);

		this.generateUser();
		TestingUsers user2 = testUsers.get(1);
		this.login(user2);

		this.generateEvent(user.token);
		TestingEvents event = testEvents.get(0);

		this.eventController.API_AddUserToEvent(user2.token, event.UUID);

		try {
			String result = this.eventController.API_LeaveEvent(user.token, event.UUID);
			String expectedResult = String.format("Successfully left event.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : EventOwnerDeletingAccount");
			System.out.println(e);
		}

		// Check DB
		EventsModel model = new EventsModel();
		// Check event
		EventData eventDB = model.getEventByUUID(event.UUID);
		assertEquals(event.UUID, eventDB.whatsUUID().toString());

		// Check HasUser
		List<HasUsersRecord> records = model.getHasUsers(event.UUID);
		assertEquals(1, records.size());
		 ;

		// Remove unnecessary data
		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
		testEvents.clear();
	}

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

		this.eventController.API_AddUserToEvent(user2.token, event.UUID);

		try {
			String result = this.userController.API_DeleteUser(user2.token);
			String expectedResult = String.format("User successfully deleted.");
			assertEquals(expectedResult, result);
		}catch(Exception e){
			System.out.println("API Tests : participantDeletesAccount");
			System.out.println(e);
		}

		// Check DB
		EventsModel eventsModel = new EventsModel();
		UserModel userModel = new UserModel();

		// Check User
		User response = userService.getUserByUUID(user2.UUID);

		// Check HasUsers
		List<HasUsersRecord> records = eventsModel.getHasUsers(event.UUID, user2.UUID);

		// Check event
		EventData DBreturn = eventsModel.getEventByUUID(event.UUID);

		assertEquals(null, response);
		assertEquals(0, records.size());
		assertEquals(event.UUID, DBreturn.whatsUUID().toString());

		// Remove unnecessary data
		UserDBChecker Checkmodel = new UserDBChecker();
		Checkmodel.removeUserByEmail(user.email);
		Checkmodel.removeUserByEmail(user2.email);
		 
		testUsers.clear();

		EventDBChecker eventModel = new EventDBChecker();
		eventModel.removeEventByUUID(event.UUID);
		 
		testEvents.clear();
	}

//	@Test
//	@Order(11)
//	public void NonOwnerDeletingEvent(){
//		// Test deleting an event while not being the owner
//	}

	// Test event owner joining event

	@Test
	@Order(22)
	public void cleanup(){
		// Remove test records from DB
		UserDB.clearTestUsers();
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
