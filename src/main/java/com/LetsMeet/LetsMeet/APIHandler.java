package com.LetsMeet.LetsMeet;

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

    @PostMapping("/api/User")
    public void API_AddUser(){

    }

    @PostMapping("api/Event")
    public void API_AddEvent(@RequestParam(value="Name") String Name, @RequestParam(value="Desc") String desc, @RequestParam(value="Location") String location){
        EventHandler.createEvent(Name, desc, location);
    }
}
