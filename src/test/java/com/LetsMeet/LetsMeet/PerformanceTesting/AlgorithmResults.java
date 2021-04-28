package com.LetsMeet.LetsMeet.PerformanceTesting;

import com.LetsMeet.LetsMeet.DBChecks.UserDBChecker;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerAPI;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResult;
import com.LetsMeet.LetsMeet.Event.Service.EventResultService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.TestingTools.TestingUsers;
import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlgorithmResults {
    private static ArrayList<TestingUsers> testUsers = new ArrayList<>();

    @Autowired
    UserControllerAPI userController;

    @Autowired
    EventControllerAPI eventController;

    @Autowired
    UserDBChecker UserDB;

    @Autowired
    EventResultService resultService;

    @Autowired
    EventService eventService;

    @Test
    @Order(1)
    public void algorithmPerformanceTest(){
        int amountUsersToBeGenerated = 0;
        TestingUsers user;

        for(int i = 0; i < amountUsersToBeGenerated; i ++){
            // Create user
            this.generateUser();
            user = testUsers.get(testUsers.size() - 1);

            // Invite user to event
            eventController.addUser("7fc4a934-4398-4e72-b664-a9fded0efeb3", user.UUID.toString(), false);

            // Give responses
            // Random number or time ranges
            int numTimes = (int)Math.floor(Math.random()*(10-2)+1);
            List<HashMap<String, String>> times = new ArrayList<>();
            HashMap<String, String> ranges = new HashMap<>();
            List<Integer> possibleMinutes = new ArrayList<>();
            possibleMinutes.add(0);
            possibleMinutes.add(15);
            possibleMinutes.add(30);
            possibleMinutes.add(45);

            int startHour;
            int endHour;
            int date;
            int startMin;
            int endMin;

            String startHourStr;
            String endHourStr;
            String dateStr;
            String startMinStr;
            String endMinStr;

            String startStr;
            String endStr;

            for(int j = 0; j < numTimes; j++){
                // Generate random time range
                ranges.clear();

                // populate ranges
                date = (int)Math.floor(Math.random()*(9-4)+1);

                // Get times
                startHour = (int)Math.floor(Math.random()*(23-2)+1);
                startMin = possibleMinutes.get((int)Math.floor(Math.random()*(3-1)+0));
                endMin = possibleMinutes.get((int)Math.floor(Math.random()*(3-1)+0));
                if(startHour == 23){
                    endHour = 23;
                    if(startMin == 45){
                        startMin = 0;
                    }

                    if(endMin == 0){
                        endMin = 45;
                    }

                }else {
                    endHour = (int) Math.floor(Math.random() * (23 - startHour + 1) + startHour);
                }

                // Format strings
                dateStr = Integer.toString(date);
                if(dateStr.length() == 1){
                    dateStr = "0" + dateStr;
                }

                startHourStr = Integer.toString(startHour);
                if(startHourStr.length() == 1){
                    startHourStr = "0" + startHourStr;
                }

                endHourStr = Integer.toString(endHour);
                if(endHourStr.length() == 1){
                    endHourStr = "0" + endHourStr;
                }

                startMinStr = Integer.toString(startMin);
                if(startMinStr.length() == 1){
                    startMinStr = "0" + startMinStr;
                }

                endMinStr = Integer.toString(endMin);
                if(endMinStr.length() == 1){
                    endMinStr = "0" + endMinStr;
                }

                startStr = "2010-04-" + dateStr + "T" + startHourStr + ":" + startMinStr + ":00" + "Z";
                endStr = "2010-04-" + dateStr + "T" + endHourStr + ":" + endMinStr + ":00" + "Z";

                // populate ranges
                ranges.put("start", startStr); // Start, end
                ranges.put("end", endStr);

                times.add(ranges);
            }
            // Send response
            eventController.responseTimeSet(user.token, "7fc4a934-4398-4e72-b664-a9fded0efeb3", times);
        }

        // Run test
        // Login as caelmilne2001@gmail.com

        System.out.println("Starting test");
        long totalTime;

        // Start timer
        Event event = eventService.get(UUID.fromString("7fc4a934-4398-4e72-b664-a9fded0efeb3")).get();
        long start = System.nanoTime();

        EventResult response = resultService.calculateTimes(event, 60, false);

        // Stop timer
        long end = System.nanoTime();
        long timeTaken = (end - start)/1000000; // milliseconds

        // Print results
        ZonedDateTime resultStart;
        ZonedDateTime resultEnd;

        for(var t : response.getDates().getGradedProperties()){
            resultStart = t.getProperty().getStart();
            resultEnd = t.getProperty().getEnd();
           System.out.println(String.format("Start: " + resultStart.getHour() + ":" + resultStart.getMinute() + " -> End: " +
                   resultEnd.getHour() + ":" + resultEnd.getMinute()));
        }

        System.out.println(String.format("Time Taken: " + timeTaken));
    }

    // Private methods
    private void generateUser(){
        // Create users with same password
        // Create a user and add it to list
        // Generate user details
        String fName = RandomStringUtils.randomAlphabetic(8);
        String lName = RandomStringUtils.randomAlphabetic(8);
        String email = String.format("Testing" + RandomStringUtils.randomAlphabetic(10) + "@InternalTesting.com");
        String password = "Testing";

        // Run method
        this.userController.API_AddUser(fName, lName, email, password);

        TestingUsers user = new TestingUsers(fName, lName, email, password);

        user.UUID = UserDB.UserUUIDFromEmail(email);

        user.token = this.userController.API_Login(user.email, user.password);
        testUsers.add(user);
        System.out.println("Generated user");
    }

    private void login(TestingUsers user){
        String token = this.userController.API_Login(user.email, user.password);
        user.token = token;
    }
}
