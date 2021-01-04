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
    public void API_AddUser(){

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
