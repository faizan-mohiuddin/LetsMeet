package com.LetsMeet.LetsMeet.Business.Controller;

import com.LetsMeet.LetsMeet.Business.Model.*;
import com.LetsMeet.LetsMeet.Business.Service.*;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BusinessControllerAPI {
    @Autowired
    BusinessService businessService;

    @Autowired
    ValidationService userValidation;

    @GetMapping("api/MyBusinesses")
    public List<Business> API_getMyBusinesses(@RequestParam(value="Token") String token){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            return null;
        }else{
            // return (String) response[1];
            return null;
        }
    }

    @PostMapping("api/Business")
    public String API_CreateBusiness(@RequestParam(value="Token") String token, @RequestParam(value="Name") String name){
        // Validate user token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result) {
            return businessService.createBusiness(name, userValidation.getUserFromToken(token));
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
