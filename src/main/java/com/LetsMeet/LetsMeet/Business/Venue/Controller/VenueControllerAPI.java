package com.LetsMeet.LetsMeet.Business.Venue.Controller;

import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VenueControllerAPI {
    @Autowired
    ValidationService userValidation;

    @Autowired
    VenueService venueService;

    @PostMapping("api/Venue")
    public String API_createVenue(@RequestParam(value="Token") String token, @RequestParam(value="BusinessID") String businessUUID,
                                  @RequestParam(value="Name") String Name){
         // Check token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            return venueService.createVenue();
        }else{
            return (String) response[1];
        }
    }
}
