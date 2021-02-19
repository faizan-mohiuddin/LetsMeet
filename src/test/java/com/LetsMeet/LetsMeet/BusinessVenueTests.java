package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.Business.Controller.BusinessControllerAPI;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.DBChecks.BusinessDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.EventDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.TestingTools.TestingBusiness;
import com.LetsMeet.LetsMeet.TestingTools.TestingEvents;
import com.LetsMeet.LetsMeet.TestingTools.TestingUsers;
import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BusinessVenueTests {
    @Autowired
    private UserControllerAPI userController;

    @Autowired
    private EventControllerAPI eventController;

    @Autowired
    private BusinessControllerAPI businessController;

    @Autowired
    UserDBChecker UserDB;

    @Autowired
    EventDBChecker EventDB;

    @Autowired
    BusinessDBChecker businessChecker;

    @Autowired
    BusinessDAO businessDB;

    @Autowired
    BusinessOwnerDAO ownerDB;

    private static ArrayList<TestingUsers> testUsers = new ArrayList<>();
    private static ArrayList<TestingEvents> testEvents = new ArrayList<>();
    private static ArrayList<TestingBusiness> testBusiness = new ArrayList<>();


    @Test
    @Order(1)
    public void createBusiness(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        String businessName = RandomStringUtils.randomAlphabetic(8);
        String result = businessController.API_CreateBusiness(user.token, businessName);

        // Check result
        assertEquals("Business created successfully", result);

        // Check DB
        Optional<Collection<BusinessOwner>> response = ownerDB.get(user.UUID);
        assertEquals(true, response.isPresent());

        List<BusinessOwner> businesses = new ArrayList(response.get());
        assertEquals(1, businesses.size());
        assertEquals(user.UUID, businesses.get(0).getUserUUID().toString());

        Optional<Business> business = businessDB.get(businesses.get(0).getBusinessUUID());
        System.out.println(businesses.get(0).getBusinessUUID());
        assertEquals(true, business.isPresent());
        assertEquals(businesses.get(0).getBusinessUUID(), business.get().getUUID());
        assertEquals(businessName, business.get().getName());

        testUsers.clear();
        testBusiness.clear();
    }

    @Test
    @Order(2)
    public void deleteBusiness(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        // Delete event
        String response = businessController.API_DeleteBusiness(user.token, business.UUID);
        assertEquals("Business successfully deleted", response);

        // Check DB
        Optional<Collection<BusinessOwner>> ownerTable = ownerDB.get(user.UUID);
        assertEquals(0, ownerTable.get().size());

        Optional<Business> businessTable = businessDB.get(UUID.fromString(business.UUID));
        assertEquals(false, businessTable.isPresent());

        testUsers.clear();
        testBusiness.clear();
    }

    @Test
    @Order(3)
    public void editBusiness(){

    }

    @Test
    @Order(4)
    public void createVenue(){

    }

    @Test
    @Order(5)
    public void deleteVenue(){

    }

    @Test
    @Order(6)
    public void editVenue(){

    }

    @Test
    @Order(7)
    public void ownerDeleteAccount(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        // Remove user account
        String response = userController.API_DeleteUser(user.token);
        assertEquals("User successfully deleted.", response);

        // Check DB
        Optional<Collection<BusinessOwner>> ownerTable = ownerDB.get(user.UUID);
        assertEquals(0, ownerTable.get().size());

        Optional<Business> businessTable = businessDB.get(UUID.fromString(business.UUID));
        assertEquals(false, businessTable.isPresent());

        testBusiness.clear();
        testUsers.clear();
    }

    // Get user businesses

    @Test
    @Order(50)
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

    public void generateBusiness(String token){
        String name = RandomStringUtils.randomAlphabetic(8);

        this.businessController.API_CreateBusiness(token, name);

        TestingBusiness business = new TestingBusiness(name);
        business.UUID = businessChecker.businessUUIDFromName(name);

        testBusiness.add(business);
    }

    private void login(TestingUsers user){
        String token = this.userController.API_Login(user.email, user.password);
        user.token = token;
    }

}
