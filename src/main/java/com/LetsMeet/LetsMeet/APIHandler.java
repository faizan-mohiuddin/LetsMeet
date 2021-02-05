package com.LetsMeet.LetsMeet;

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

    // Event routes here
    @GetMapping("api/Events")
    public List<AdminEventData> API_GetAllEvents(){
        return RequestHandler.getAllEvents();
    }

    @GetMapping("api/MyEvents")
    public List<EventData> API_GetMyEvents(@RequestParam(value="Token") String token) {
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user UUID
                String UserUUID = userValidation.getUserUUIDfromToken(token);
                return RequestHandler.getMyEvents(UserUUID);
        }else{
            String errorText = (String) response[1];
            EventData error = new EventData(errorText, errorText, errorText, errorText);
            List<EventData> ErrorReturn = new ArrayList<>();
            ErrorReturn.add(error);
            return ErrorReturn;
        }
    }

    // Get event by specific eventUUID
    @GetMapping("api/Event/{UUID}")
    public EventData API_GetEvent(@PathVariable(value="UUID") String UUID){
        return RequestHandler.getEvent(UUID);
    }

    // Create event
    @PostMapping("api/Event")
    public String API_AddEvent(@RequestParam(value="Name") String Name, @RequestParam(value="Desc") String desc,
                             @RequestParam(value="Location") String location,
                             @RequestParam(value="Token", defaultValue="") String token){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserSanitised user = userValidation.getUserFromToken(token);
            if(user == null){
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }
            return RequestHandler.createEvent(Name, desc, location, user.getStringUUID());
        }else{
            String errorText = (String) response[1];
            return errorText;
        }

    }

    // Join event
    @PutMapping("api/Event/{EventUUID}")
    public String API_AddUserToEvent(@RequestParam(value="Token", defaultValue ="") String token,
                                     @PathVariable(value="EventUUID") String EventUUID) {
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserSanitised user = userValidation.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Add user to event
            return RequestHandler.joinEvent(EventUUID, user.getStringUUID());

        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }

    @PutMapping("api/Event/{EventUUID}/Leave")
    public String API_LeaveEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){
        // Validate token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserSanitised user = userValidation.getUserFromToken(token);

            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Leave event
            return RequestHandler.leaveEvent(EventUUID, user.getStringUUID());

        }else{
            return "Token not valid.";
        }
    }

    // Delete event
    @DeleteMapping("api/Event/{EventUUID}")
    public String API_DeleteEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){

        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user
                UserSanitised user = userValidation.getUserFromToken(token);

                if (user == null) {
                    return "Error finding user. Is the token still valid? Is the user account still active?";
                }

                // Add user to event
                return RequestHandler.deleteEvent(EventUUID, user.getStringUUID());

        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }
    // End of event routes

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
