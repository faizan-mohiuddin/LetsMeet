package com.LetsMeet.LetsMeet.Business.Venue.Controller;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueSanitised;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueBusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VenueControllerAPI {
    @Autowired
    ValidationService userValidation;

    @Autowired
    VenueService venueService;

    @Autowired
    VenueBusinessService venueBusinessService;

    @Autowired
    BusinessService businessService;

    @PostMapping("api/Venue")
    public String API_createVenue(@RequestParam(value="Token") String token, @RequestParam(value="BusinessID") String businessUUID,
                                  @RequestParam(value="Name") String Name){
         // Check token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            return venueService.createVenue(user, Name, businessUUID);
        }else{
            return (String) response[1];
        }
    }

    @DeleteMapping("api/Venue")
    public String API_deleteVenue(@RequestParam(value="Token") String token, @RequestParam(value="VenueID") String venueUUID){
        // Check token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            return venueService.deleteVenue(user, venueUUID);
        }else{
            return (String) response[1];
        }
    }

    @GetMapping("api/Business/Venues")
    public String API_getBusinessVenues(@RequestParam(value="BusinessID") String businessUUID){
        // Check businessUUID is valid
        Business business = businessService.getBusiness(businessUUID);

        if(business != null) {
            // Get venues
            List<Venue> venues = venueBusinessService.getBusinessVenues(businessUUID);

            ObjectMapper mapper = new ObjectMapper();
            try {
                String resultString = mapper.writeValueAsString(venues);
                return resultString;
            }catch(Exception e){
                return "An error occured";
            }
        }
        return "Invalid BusinessID";
    }

    @PatchMapping
    public String API_updateVenue(@RequestParam(value="Token") String token, @RequestParam(value="VenueID") String venueUUID,
                                  @RequestParam(value="Name") String name){
        return null;
    }

    @GetMapping("api/Venue")
    public VenueSanitised API_getVenue(@RequestParam(value="venueID") String venueUUID){
        Venue v = venueService.getVenue(venueUUID);
        if(v != null){
            return v.convertToSanitised();
        }else{
            return null;
        }
    }
}
