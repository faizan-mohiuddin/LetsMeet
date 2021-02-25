package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.DBChecks.EventDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.TestingTools.TestingEvents;
import com.LetsMeet.LetsMeet.TestingTools.TestingUsers;
import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ValidationTests {
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
    EventDao eventModel;

    @Autowired
    EventPermissionDao EventPermissionModel;

    private static ArrayList<TestingUsers> testUsers = new ArrayList<>();
    private static ArrayList<TestingEvents> testEvents = new ArrayList<>();

    @Test
    @Order(1)
    public void NonOwnerDeletingEvent(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateEvent(user.token);
        TestingEvents event = testEvents.get(0);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);
        this.eventController.API_AddUserToEvent(user2.token, event.UUID);

        this.generateUser();
        TestingUsers user3 = testUsers.get(2);
        this.login(user3);
        this.eventController.API_AddUserToEvent(user3.token, event.UUID);

        String result = this.eventController.API_DeleteEvent(user2.token, event.UUID);

        // Check response
        assertEquals("You do not have permission to delete this event", result);

        result = this.eventController.API_DeleteEvent(user3.token, event.UUID);
        assertEquals("You do not have permission to delete this event", result);

        // Check Event is still in DB
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writeValueAsString(this.eventController.API_GetEvent(event.UUID));
            String expectedResut = String.format("{\"name\":\"%s\",\"description\":\"%s\",\"location\":\"%s\"}",
                    event.name, event.desc, event.location);
            assertEquals(expectedResut, result);
        }catch (Exception e){
            System.out.println("Validation tests : NonOwnerDeletingEvent");
            System.out.println(e);
        }

        this.clearData();
    }

    @Test
    @Order(2)
    public void EventOwnerJoiningEvent(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateEvent(user.token);
        TestingEvents event = testEvents.get(0);

        String result = this.eventController.API_AddUserToEvent(user.token, event.UUID);
        String expectedResult = "You are already a participant of this event.";
        assertEquals(expectedResult, result);

        // Check DB
        Optional<List<EventPermission>> response = EventPermissionModel.get(event.UUID);
        assertEquals(true, response.isPresent());
        assertEquals(1, response.get().size());

        this.clearData();
    }

    @Test
    @Order(3)
    public void EventParticipantJoiningEvent(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateEvent(user.token);
        TestingEvents event = testEvents.get(0);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);

        this.eventController.API_AddUserToEvent(user2.token, event.UUID);
        String result = this.eventController.API_AddUserToEvent(user2.token, event.UUID);
        String expectedResult = "You are already a participant of this event.";
        assertEquals(expectedResult, result);

        // Check DB
        Optional<List<EventPermission>> response = EventPermissionModel.get(event.UUID);
        assertEquals(true, response.isPresent());
        assertEquals(2, response.get().size());

        this.clearData();
    }

    @Test
    @Order(4)
    public void UserLeavingEventTheyAreNotPartOf(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateEvent(user.token);
        TestingEvents event = testEvents.get(0);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);

        String result = this.eventController.API_LeaveEvent(user2.token, event.UUID);
        String expectedResult = "You have not joined this event";
        assertEquals(expectedResult, result);

        // Check DB
        Optional<List<EventPermission>> response = EventPermissionModel.get(event.UUID);
        assertEquals(true, response.isPresent());
        assertEquals(1, response.get().size());

        this.clearData();
    }

    @Test
    @Order(5)
    public void NonOwnerDeleteEvent(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateEvent(user.token);
        TestingEvents event = testEvents.get(0);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);

        String expectedResult = "You do not have permission to delete this event";

        // Test before joining event
        String result = eventController.API_DeleteEvent(user2.token, event.UUID);
        assertEquals(expectedResult, result);

        eventController.API_AddUserToEvent(user2.token, event.UUID);

        // Test after joining event
        result = eventController.API_DeleteEvent(user2.token, event.UUID);
        assertEquals(expectedResult, result);

        this.clearData();
    }

    // Check only event owner can update event


    // Check who can delete business
    // Check only condition set owner can update condition set
    // Check who can create venue on behalf of business
    // Check who can delete venue


    @Test
    @Order(60)
    public void cleanup(){
        // Remove test records from DB
        UserDB.clearTestData();
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

    private void clearData(){
        for(TestingUsers user : testUsers){
            UserDB.removeUserByEmail(user.email);
        }
        testUsers.clear();

        for(TestingEvents event : testEvents){
            EventDB.removeEventByUUID(event.UUID);
        }
        testEvents.clear();
    }
}
