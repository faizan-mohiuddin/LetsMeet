package com.LetsMeet.LetsMeet.Interface;

import com.LetsMeet.LetsMeet.RequestHandler;
import com.LetsMeet.LetsMeet.UserManager.UserManager;
import com.LetsMeet.Models.Data.AdminEventData;
import com.LetsMeet.Models.Data.AdminUserData;
import com.LetsMeet.Models.Data.EventData;
import com.LetsMeet.Models.Data.UserData;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class APIHandler {

    // This will return list of commands?
    @GetMapping("/api/Home")
    public String API_Home(){
        return "Welcome to the lets meet API! \nFor more information on using the API service visit the API help page";
    }

    // User routes here
    @PostMapping("/api/User")
    public String API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                            @RequestParam(value="email") String email, @RequestParam(value="password") String password){
         return RequestHandler.createUser(fName, lName, email, password);
    }

    // User login
    @PostMapping("/api/login")
    public String API_Login(@RequestParam(value="email") String email, @RequestParam(value="password") String password){
        UserData user = RequestHandler.validate(email, password);
        if(user != null) {
            // If true API token is returned
            return RequestHandler.getUserToken(user);
        }else{
            return "Error, invalid email or password";
        }
    }

    // Delete user account
    @DeleteMapping("/api/User")
    public String API_DeleteUser(@RequestParam(value="Token") String token){
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            String UserUUID = RequestHandler.getUserUUIDfromToken(token);

            // Delete user
            return RequestHandler.deleteUser(UserUUID);
        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }

    // Get a users data
    @GetMapping("/api/User")
    public UserData API_GetUser(@RequestParam(value="Token") String token){
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            UserData user = RequestHandler.getUserFromToken(token);
            return user;
        }else{
            String errorText = (String) response[1];
            UserData errorData = new UserData();
            errorData.populate(errorText, errorText, errorText, errorText, errorText, errorText);
            return errorData;
        }
    }

    // Update User
    @PatchMapping("/api/User")
    public String API_UpdateUser(@RequestParam(value="Token") String token,
                                 @RequestParam(value="fName", defaultValue="") String fName,
                                 @RequestParam(value="lName", defaultValue="") String lName,
                                 @RequestParam(value="email", defaultValue="") String email){
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            AdminUserData user = RequestHandler.getUserFromTokenWithAdmin(token);
            return RequestHandler.updateUser(user, fName, lName, email);
        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }
    // End of user routes

    // Event routes here
    @GetMapping("api/Events")
    public List<AdminEventData> API_GetAllEvents(){
        return RequestHandler.getAllEvents();
    }

    @GetMapping("api/MyEvents")
    public List<EventData> API_GetMyEvents(@RequestParam(value="Token") String token) {
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user UUID
                String UserUUID = RequestHandler.getUserUUIDfromToken(token);
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
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserData user = UserManager.getUserFromToken(token);
            if(user == null){
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }
            return RequestHandler.createEvent(Name, desc, location, user.whatsUUID());
        }else{
            String errorText = (String) response[1];
            return errorText;
        }

    }

    // Join event
    @PutMapping("api/Event/{EventUUID}")
    public String API_AddUserToEvent(@RequestParam(value="Token", defaultValue ="") String token,
                                     @PathVariable(value="EventUUID") String EventUUID) {
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserData user = UserManager.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Add user to event
            return RequestHandler.joinEvent(EventUUID, user.whatsUUID());

        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }

    @PutMapping("api/Event/{EventUUID}/Leave")
    public String API_LeaveEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){
        // Validate token
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            UserData user = UserManager.getUserFromToken(token);

            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Leave event
            return RequestHandler.leaveEvent(EventUUID, user.whatsUUID());

        }else{
            return "Token not valid.";
        }
    }

    // Delete event
    @DeleteMapping("api/Event/{EventUUID}")
    public String API_DeleteEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){

        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user
                UserData user = UserManager.getUserFromToken(token);

                if (user == null) {
                    return "Error finding user. Is the token still valid? Is the user account still active?";
                }

                // Add user to event
                return RequestHandler.deleteEvent(EventUUID, user.whatsUUID());

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
        Object[] response = UserManager.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
                // Get user
                UserData user = UserManager.getUserFromToken(token);
                RequestHandler.NewConditionSet(EventUUID, user.whatsUUID(), setName);

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
