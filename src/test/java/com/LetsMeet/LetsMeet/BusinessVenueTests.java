package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.Business.Controller.BusinessControllerAPI;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Venue.Controller.VenueControllerAPI;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.DBChecks.BusinessDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.EventDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.DBChecks.VenueDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.TestingTools.TestingBusiness;
import com.LetsMeet.LetsMeet.TestingTools.TestingEvents;
import com.LetsMeet.LetsMeet.TestingTools.TestingUsers;
import com.LetsMeet.LetsMeet.TestingTools.TestingVenue;
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
    private VenueControllerAPI venueController;

    @Autowired
    UserDBChecker UserDB;

    @Autowired
    EventDBChecker EventDB;

    @Autowired
    BusinessDBChecker businessChecker;

    @Autowired
    VenueDBChecker venueDBChecker;

    @Autowired
    BusinessDAO businessDB;

    @Autowired
    BusinessOwnerDAO ownerDB;

    @Autowired
    VenueBusinessDAO venueBusinessDAO;

    @Autowired
    VenueDAO venueDAO;

    private static ArrayList<TestingUsers> testUsers = new ArrayList<>();
    private static ArrayList<TestingEvents> testEvents = new ArrayList<>();
    private static ArrayList<TestingBusiness> testBusiness = new ArrayList<>();
    private static ArrayList<TestingVenue> testVenue = new ArrayList<>();


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
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        String venueName = RandomStringUtils.randomAlphabetic(8);
        String response = venueController.API_createVenue(user.token, business.UUID, venueName);
        assertEquals("Venue created successfully", response);

        // Check DB
        Optional<Collection<VenueBusiness>> venues = venueBusinessDAO.getBusinessVenues(business.UUID);
        assertEquals(true, venues.isPresent());
        assertEquals(1, venues.get().size());

        VenueBusiness venue = (VenueBusiness) venues.get().toArray()[0];
        Optional<Venue> responseVenue = venueDAO.get(venue.getVenueUUID());
        assertEquals(true, responseVenue.isPresent());
        assertEquals(venueName, responseVenue.get().getName());

        Optional<VenueBusiness> hasVenue = venueBusinessDAO.get(UUID.fromString(business.UUID), responseVenue.get().getUUID());
        assertEquals(true, hasVenue.isPresent());
        assertEquals(responseVenue.get().getUUID(), hasVenue.get().getVenueUUID());
        assertEquals(UUID.fromString(business.UUID), hasVenue.get().getBusinessUUID());
    }

    @Test
    @Order(5)
    public void deleteVenue(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        String response = venueController.API_deleteVenue(user.token, venue.uuid);
        assertEquals("Venue successfully deleted", response);

        // Check DB
        Optional<VenueBusiness> hasVenue = venueBusinessDAO.get(UUID.fromString(business.UUID), UUID.fromString(venue.uuid));
        assertEquals(false, hasVenue.isPresent());

        testUsers.clear();
        testBusiness.clear();
        testVenue.clear();
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

    // When business owner deletes account, check venue is deleted as well (when there is no owners left with accounts)
    @Test
    @Order(8)
    public void venueOwnerDeletesAccount(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        // Remove user account
        String response = userController.API_DeleteUser(user.token);
        assertEquals("User successfully deleted.", response);

        // Check HasVenues and Venues table
        Optional<Venue> venueResponse = venueDAO.get(UUID.fromString(venue.uuid));
        assertEquals(false, venueResponse.isPresent());

        Optional<VenueBusiness> businessVenueResponse = venueBusinessDAO.get(UUID.fromString(business.UUID), UUID.fromString(venue.uuid));
        assertEquals(false, businessVenueResponse.isPresent());

        testUsers.clear();
        testBusiness.clear();
        testVenue.clear();
    }

    @Test
    @Order(9)
    public void joinBusiness(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        String response = businessController.API_JoinBusiness(user2.token, business.UUID);
        assertEquals("Successfully joined Business", response);

        // Check DB
        Optional<Collection<BusinessOwner>> owners = ownerDB.get(business.UUID);
        assertEquals(true, owners.isPresent());
        assertEquals(2, owners.get().size());

        testUsers.clear();
        testBusiness.clear();
    }

    // Business owner deletes account, but another user is part of business
    @Test
    @Order(10)
    public void venueCoOwnerDeletesAccount(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        // Add another user to business
        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        businessController.API_JoinBusiness(user2.token, business.UUID);

        // Remove user account
        String response = userController.API_DeleteUser(user.token);
        assertEquals("User successfully deleted.", response);

        // Check Business, HasBusiness, HasVenues and Venues table
        Optional<Business> businessResponse = businessDB.get(UUID.fromString(business.UUID));
        assertEquals(true, businessResponse.isPresent());
        assertEquals(business.name, businessResponse.get().getName());
        assertEquals(business.UUID, businessResponse.get().getUUID().toString());

        Optional<Collection<BusinessOwner>> owner = ownerDB.get(business.UUID);
        assertEquals(true, owner.isPresent());
        assertEquals(1, owner.get().size());
        BusinessOwner o = (BusinessOwner) owner.get().toArray()[0];
        assertEquals(user2.UUID, o.getUserUUID().toString());
        assertEquals(business.UUID, o.getBusinessUUID().toString());

        Optional<Venue> venueResponse = venueDAO.get(UUID.fromString(venue.uuid));
        assertEquals(true, venueResponse.isPresent());
        assertEquals(venue.name, venueResponse.get().getName());
        assertEquals(venue.uuid, venueResponse.get().getUUID().toString());

        Optional<VenueBusiness> businessVenueResponse = venueBusinessDAO.get(UUID.fromString(business.UUID), UUID.fromString(venue.uuid));
        assertEquals(true, businessVenueResponse.isPresent());
        assertEquals(venue.uuid, businessVenueResponse.get().getVenueUUID().toString());
        assertEquals(business.UUID, businessVenueResponse.get().getBusinessUUID().toString());

        testUsers.clear();
        testBusiness.clear();
        testVenue.clear();
    }

    // Get user businesses
    // Get business venues
    // When business is deleted - venues are deleted
    // Create venue with invalid business

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

    private void generateVenue(String userToken, String businessUUID){
        String name = RandomStringUtils.randomAlphabetic(8);
        this.venueController.API_createVenue(userToken, businessUUID, name);
        TestingVenue venue = new TestingVenue(venueDBChecker.venueUUIDFromNameandBusinessUUID(name, businessUUID), name);

        testVenue.add(venue);
    }

}
