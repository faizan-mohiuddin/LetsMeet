package com.LetsMeet.LetsMeet.PerformanceTesting;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.Venue.Controller.VenueControllerAPI;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Service.VenueService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VenueSearching {

    @Autowired
    VenueControllerAPI venueAPI;

    @Autowired
    VenueService venueService;

    @Autowired
    UserService userService;

    @Test
    @Order(1)
    public void timeTest(){
        long start = System.nanoTime();
//        String searchVenue = "bar";
//        int target = 10;

        String searchVenue = "bar,shag pad";
        int target = 5;

        List<Venue> response = venueAPI.API_SearchVenue("", searchVenue, "", "", "", "",
                "", "", "", "");
        long end = System.nanoTime();
        long timeTaken = (end - start)/1000000;

        System.out.println(response.size());
        System.out.println(target);
        System.out.println(response.size() == target);

        System.out.println(timeTaken);
    }

    @Test
    @Order(999)
    public void generateVenues(){
        int num = 9000;    // Enter how many venues to be created here

        // Get user account
        User user = userService.getUserByUUID("98611458-0f33-31ca-a56d-9162af35a45a");

        for(int i = 0; i < num; i++){
            venueService.createVenue(user, generateWord(), "0f9376de-54c0-3c16-b67a-87d9c6587a79");
        }
    }

    // Private methods
    private String generateWord(){
        // Random length word
        String word = RandomStringUtils.randomAlphabetic((int)Math.floor(Math.random()*(20-2)+1));
        return "Testing"+word;
    }

}
