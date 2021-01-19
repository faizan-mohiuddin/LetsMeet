package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.UserData;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class APIHandler {

    // This will return list of commands?
    @GetMapping("/api/Home")
    public String API_Home(){
        return "Api Home";
    }

    // User routes here
    @PostMapping("/api/User")
    public String API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                            @RequestParam(value="email") String email, @RequestParam(value="password") String password){
         return EventHandler.createUser(fName, lName, email, password);
    }

    // User login
    @PostMapping("/api/login")
    public String API_Login(@RequestParam(value="email") String email, @RequestParam(value="password") String password){
        UserData user = EventHandler.validate(email, password);
        if(user != null) {
            // If true API token is returned
            return EventHandler.getUserToken(user);
        }else{
            return "Error, invalid email or password";
        }
    }

    // End of user routes

    // Event routes here
    @GetMapping("api/Events")
    public List<EventData> API_GetAllEvents(){
        return EventHandler.getAllEvents();
    }

    // Get event by specific eventUUID
    @GetMapping("api/Event/{UUID}")
    public EventData API_GetEvent(@PathVariable(value="UUID") String UUID){
        return EventHandler.getEvent(UUID);
    }

    // Create event
    @PostMapping("api/Event")
    public String API_AddEvent(@RequestParam(value="Name") String Name, @RequestParam(value="Desc") String desc,
                             @RequestParam(value="Location") String location,
                             @RequestParam(value="Token", defaultValue="") String token){
        if(token.equals("")){
            return "API token required";
        }else {
            // Check token is valid
            boolean valid = EventHandler.checkValidAPIToken(token);

            if(valid){
                // Get user
                UserData user = UserManager.getUserFromToken(token);
                if(user == null){
                    return "Error finding user. Is the token still valid? Is the user account still active?";
                }

                return EventHandler.createEvent(Name, desc, location, user.getUserUUID());
            }else{
                return "Invalid token. Token may have expired";
            }

        }
    }

    // Join event
    @PutMapping("api/Event/{EventUUID}")
    public String API_AddUserToEvent(@RequestParam(value="Token", defaultValue ="") String token,
                                     @PathVariable(value="EventUUID") String EventUUID) {
        // Validate API token
        if (token.equals("")) {
            return "API token required";
        } else {
            // Check token is valid
            boolean valid = EventHandler.checkValidAPIToken(token);

            if (valid) {
                // Get user
                UserData user = UserManager.getUserFromToken(token);

                if (user == null) {
                    return "Error finding user. Is the token still valid? Is the user account still active?";
                }

                // Add user to event
                return EventHandler.joinEvent(EventUUID, user.getUserUUID());

            } else {
                return "Invalid token. Token may have expired";
            }
        }
    }

    @DeleteMapping("api/Event/{EventUUID}")
    public String API_DeleteEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){

        if (token.equals("")){
            return "API token required";
        } else {
            // Check token is valid
            boolean valid = EventHandler.checkValidAPIToken(token);

            if (valid) {
                // Get user
                UserData user = UserManager.getUserFromToken(token);

                if (user == null) {
                    return "Error finding user. Is the token still valid? Is the user account still active?";
                }

                // Add user to event
                return EventHandler.deleteEvent(EventUUID, user.getUserUUID());

            } else {
                return "Invalid token. Token may have expired";
            }
        }
    }
    // End of event routes

    // ConditionSet routes
    @PutMapping("api/ConditionSet")
    public String API_NewConditionSet(@RequestParam(value="EventUUID") String EventUUID, @RequestParam("Token") String token,
                                      @RequestParam(value="SetName") String setName) {
        // Verify API token
        if (token.equals("")) {
            return "API token required";
        } else {
            // Check token is valid
            boolean valid = EventHandler.checkValidAPIToken(token);

            if (valid) {
                // Get user
                UserData user = UserManager.getUserFromToken(token);
                EventHandler.NewConditionSet(EventUUID, user.getUserUUID(), setName);

                return "ConditionSet created successfully";
            }else{
                return "Invalid token. Token may have expired";
            }
        }
    }
    // End of ConditionSet routes
}
