package com.LetsMeet.LetsMeet;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class EventHandler {

    @GetMapping("/")
    public String Home(){
        return "Home";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
