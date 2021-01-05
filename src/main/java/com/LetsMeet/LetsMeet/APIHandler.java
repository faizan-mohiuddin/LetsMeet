package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventData;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class APIHandler {

    @GetMapping("/api/Home")
    public String API_Home(){
        return "Api Home";
    }

    // User routes here
    @PostMapping("/api/User")
    public void API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                            @RequestParam(value="email") String email, @RequestParam(value="password") String password){
         EventHandler.createUser(fName, lName, email, password);
    }

    @PostMapping("/api/login")
    public void API_Login(@RequestParam(value="email") String email, @RequestParam(value="password") String password){
        boolean validation = EventHandler.validate(email, password);
        // If true API token is returned
    }

    // End of user routes

    // Event routes here
    @GetMapping("api/Events")
    public List<EventData> API_GetAllEvents(){
        return EventHandler.getAllEvents();
    }

    @GetMapping("api/Event/{UUID}")
    public EventData API_GetEvent(@PathVariable(value="UUID") String UUID){
        return EventHandler.getEvent(UUID);
    }

    @PostMapping("api/Event")
    public void API_AddEvent(@RequestParam(value="Name") String Name, @RequestParam(value="Desc") String desc, @RequestParam(value="Location") String location){
        EventHandler.createEvent(Name, desc, location);
    }

    // End of event routes
}
