package com.LetsMeet.LetsMeet.Business.Controller;

import com.LetsMeet.LetsMeet.Business.Model.*;
import com.LetsMeet.LetsMeet.Business.Service.*;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class BusinessControllerAPI {
    @Autowired
    BusinessService businessService;

    @Autowired
    ValidationService userValidation;

    @GetMapping("api/MyBusinesses")
    public String API_getMyBusinesses(@RequestParam(value="Token") String token){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            Collection<Business> records = businessService.getUserBusinesses(user.getStringUUID());

            ObjectMapper mapper = new ObjectMapper();
            try {
                String resultString = mapper.writeValueAsString(records);
                return resultString;
            }catch(Exception e){
                return "An error occured";
            }
        }else{
            return (String) response[1];
        }
    }

    @GetMapping("api/Business")
    public Business API_getBusiness(@RequestParam(value="BusinessID") String businessUUID){
        return businessService.getBusiness(businessUUID);
    }

    @PostMapping("api/Business")
    public String API_CreateBusiness(@RequestParam(value="Token") String token, @RequestParam(value="Name") String name){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            Business business =businessService.createBusiness(name, userValidation.getUserFromToken(token));
            if(business == null){
                return "Error creating Business";
            }
            return "Business created successfully";
        }else{
            return (String) response[1];
        }
    }

    @DeleteMapping("api/Business")
    public String API_DeleteBusiness(@RequestParam(value="Token") String token, @RequestParam(value="Business") String businessUUID){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            return businessService.deleteBusiness(businessUUID, user.getStringUUID());
        }else{
            return (String) response[1];
        }
    }

    @PutMapping("api/Business")
    public String API_JoinBusiness(@RequestParam(value="Token") String token, @RequestParam(value="Business") String businessUUID){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            User user = userValidation.getUserFromToken(token);
            return businessService.joinBusiness(businessUUID, user.getStringUUID());
        }else{
            return (String) response[1];
        }
    }
}
