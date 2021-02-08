// Legacy code - disabled

/*package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.Models.AdminEventData;
import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class APIHandler {

    @Autowired
    private ValidationService userValidation;

    @Autowired
    private UserService userService;

    // This will return list of commands?
    @GetMapping("/api/Home")
    public String API_Home(){
        return "Welcome to the lets meet API! \nFor more information on using the API service visit ";
    }

    // ConditionSet routes
    @PutMapping("api/ConditionSet")
    public String API_NewConditionSet(@RequestParam(value="EventUUID") String EventUUID, @RequestParam("Token") String token,
                                      @RequestParam(value="SetName") String setName) {
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user
                UserSanitised user = userValidation.getUserFromToken(token);
                RequestHandler.NewConditionSet(EventUUID, user.getStringUUID(), setName);

                return "ConditionSet created successfully";
        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }
    // End of ConditionSet routes

    // Error handling
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex){
        String name = ex.getParameterName();
        return String.format(name + " Parameter is missing");
    }
    // End of Error handling
}
*/