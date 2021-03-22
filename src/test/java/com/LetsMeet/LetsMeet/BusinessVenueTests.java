package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.Business.Controller.BusinessControllerAPI;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Controller.VenueControllerAPI;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueBusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

    @Autowired
    VenueService venueService;

    @Autowired
    VenueBusinessService venueBusinessService;

    @Autowired
    BusinessService businessService;

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
        assertEquals(true, false);
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

        testUsers.clear();
        testBusiness.clear();
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
        this.generateUser();
        TestingUsers user = testUsers.get(0);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        String expectedResponse = "Event successfully updated";

        // Update name
        String name = RandomStringUtils.randomAlphabetic(8);
        String response = venueController.API_updateVenue(user.token, venue.uuid, name);
        assertEquals(expectedResponse, response);

        // Check DB
        assertEquals(true, false);
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
    @Test
    @Order(11)
    public void getUserBusinesses(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateBusiness(user.token);
        TestingBusiness business2 = testBusiness.get(1);

        // Call API method
        String response = businessController.API_getMyBusinesses(user.token);
        System.out.println(response);

        try {
            JSONArray array = new JSONArray(response);
            int matches = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject responseBusinesses = array.getJSONObject(i);

                for(TestingBusiness b : testBusiness) {
                    if (b.UUID.equals(responseBusinesses.getString("uuid"))) {
                        // Match
                        matches += 1;
                        assertEquals(b.name, responseBusinesses.getString("name"));
                    }
                }
            }
            assertEquals(2, matches);
        }catch (Exception e){
            System.out.println("BusinessVenueTests : getUserBusinesses");
            e.printStackTrace();
        }

        testBusiness.clear();
        testUsers.clear();
    }

    // Get business venues
    @Test
    @Order(12)
    public void getBusinessVenues(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue2 = testVenue.get(1);

        String response =  venueController.API_getBusinessVenues(business.UUID);
        System.out.println(response);

        try {
            JSONArray array = new JSONArray(response);
            int matches = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject responseVenue = array.getJSONObject(i);

                for(TestingVenue v : testVenue) {
                    if (v.uuid.equals(responseVenue.getString("uuid"))) {
                        // Match
                        matches += 1;
                        assertEquals(v.name, responseVenue.getString("name"));
                    }
                }
            }
            assertEquals(2, matches);
        }catch (Exception e){
            System.out.println("BusinessVenueTests : getBusinessVenues");
            e.printStackTrace();
        }
    }

    // When business is deleted - venues are deleted
    @Test
    @Order(13)
    public void deleteBusinessWithVenue(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateUser();
        TestingUsers user2 = testUsers.get(1);
        this.login(user2);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        String response = businessController.API_DeleteBusiness(user.token, business.UUID);
        assertEquals("Business successfully deleted", response);

        // Check that venues are deleted
        Venue responseVenue = venueService.getVenue(venue.uuid);
        assertEquals(null, responseVenue);

        // Check hasVenue records are deleted
        List<Venue> hasVenueResponse = venueBusinessService.getBusinessVenues(business.UUID);
        assertEquals(null, hasVenueResponse);

        // Check that HasBusiness records are deleted
        List<BusinessOwner> hasBusinessResponse = businessService.businessOwners(business.UUID);
        assertEquals(null, hasBusinessResponse);
    }

    // Create venue with invalid business
    @Test
    @Order(14)
    public void createVenueWithInvalidBusiness(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        String venueName = RandomStringUtils.randomAlphabetic(8);
        String lastChar = business.UUID.substring(business.UUID.length()-1);
        String fakeBusinessUUID = business.UUID.substring(0, business.UUID.length()-1);

        String randomChar;

        do {
            randomChar = RandomStringUtils.randomAlphabetic(1);
        }while (randomChar.equals(lastChar));

        fakeBusinessUUID = fakeBusinessUUID+randomChar;

        String response = venueController.API_createVenue(user.token, fakeBusinessUUID, venueName);
        assertEquals("Invalid businessID, cannot create Venue", response);

        // Check DB
        // Check hasVenue records are deleted
        List<Venue> hasVenueResponse = venueBusinessService.getBusinessVenues(business.UUID);
        assertEquals(null, hasVenueResponse);
    }

    // Get venue
    @Test
    @Order(15)
    public void getVenue(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String response = mapper.writeValueAsString(venueController.API_getVenue(venue.uuid));
            System.out.println(response);

            String expectedResponse = String.format("{\"Name\":\"%s\",\"Facilities\":[]}", venue.name);
            assertEquals(expectedResponse, response);

        }catch (Exception e){
            System.out.println("BusinessVenue Tests : getVenue");
            System.out.println(e);
        }
    }

    // Get business
    @Test
    @Order(16)
    public void getBusiness(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String response = mapper.writeValueAsString(businessController.API_getBusiness(business.UUID));
            System.out.println(response);

            String expectedResponse = String.format("{\"Name\":\"%s\",\"Venues\":[]}", business.name);
            assertEquals(expectedResponse, response);

        }catch (Exception e){
            System.out.println("BusinessVenue Tests : getBusiness");
            System.out.println(e);
        }
    }

    @Test
    @Order(17)
    public void addFacilityVenue(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        // generate facility tag
        String tag = RandomStringUtils.randomAlphabetic(8);
        String response = venueController.API_addFacility(user.token, venue.uuid, tag);
        assertEquals("Facility added to Venue successfully", response);

        // Get venue
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(venueController.API_getVenue(venue.uuid));

            String expectedResponse = String.format("{\"Name\":\"%s\",\"Facilities\":[\"%s\"]}", venue.name, tag);
            assertEquals(expectedResponse, response);

        }catch (Exception e){
            System.out.println("BusinessVenue Tests : addFacilityVenue");
            System.out.println(e);
        }

    }

    @Test
    @Order(18)
    public void searchVenueByName(){
        // Create a venue and search for it
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue1 = testVenue.get(0);

        // Search with the whole string
        List<Venue> response = venueController.API_SearchVenue(venue1.name, "");
        assertEquals(1, response.size());
        Venue responseVenue = response.get(0);
        assertEquals(venue1.uuid, responseVenue.getUUID().toString());
        assertEquals(venue1.name, responseVenue.getName());

        // Try searching with only the start of the string
        response = venueController.API_SearchVenue(venue1.name.substring(0, 3), "");
        assertEquals(1, response.size());
        responseVenue = response.get(0);
        assertEquals(venue1.uuid, responseVenue.getUUID().toString());
        assertEquals(venue1.name, responseVenue.getName());

        // Try searching with only the middle of the string
        response = venueController.API_SearchVenue(venue1.name.substring(3, 6), "");
        assertEquals(1, response.size());
        responseVenue = response.get(0);
        assertEquals(venue1.uuid, responseVenue.getUUID().toString());
        assertEquals(venue1.name, responseVenue.getName());

        // Try searching with only the end of the string
        response = venueController.API_SearchVenue(venue1.name.substring(5), "");
        assertEquals(1, response.size());
        responseVenue = response.get(0);
        assertEquals(venue1.uuid, responseVenue.getUUID().toString());
        assertEquals(venue1.name, responseVenue.getName());
    }

    @Test
    @Order(19)
    public void queryVenuesByFacilities(){
        this.generateUser();
        TestingUsers user = testUsers.get(0);
        this.login(user);

        this.generateBusiness(user.token);
        TestingBusiness business = testBusiness.get(0);

        this.generateVenue(user.token, business.UUID);
        TestingVenue venue = testVenue.get(0);

        // Add facilities to venue
        String tag1 = RandomStringUtils.randomAlphabetic(8);
        venueController.API_addFacility(user.token, venue.uuid, tag1);

        String tag2 = RandomStringUtils.randomAlphabetic(8);
        venueController.API_addFacility(user.token, venue.uuid, tag2);

        String tag3 = RandomStringUtils.randomAlphabetic(8);
        venueController.API_addFacility(user.token, venue.uuid, tag3);

        // Search with no name given
        String searchFacilities = String.format("[\"%s\",\"%s\",\"%s\"]", tag1, tag2, tag3);
        List<Venue> response = venueController.API_SearchVenue("", searchFacilities);
        assertEquals(1, response.size());
        Venue responseVenue = response.get(0);
        assertEquals(venue.uuid, responseVenue.getUUID().toString());
        assertEquals(venue.name, responseVenue.getName());

        // Search with name given
        response = venueController.API_SearchVenue(venue.name, searchFacilities);
        assertEquals(1, response.size());
        responseVenue = response.get(0);
        assertEquals(venue.uuid, responseVenue.getUUID().toString());
        assertEquals(venue.name, responseVenue.getName());
    }

    @Test
    @Order(20)
    public void searchByRadius(){

    }

    @Test
    @Order(50)
    public void cleanup(){
        // Remove test records from DB
        UserDB.clearTestData();
    }

    @AfterEach
    public void clearLists(){
        testUsers.clear();
        testBusiness.clear();
        testVenue.clear();
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
