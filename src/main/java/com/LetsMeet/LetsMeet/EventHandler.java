package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class EventHandler {

    @RequestMapping("/Home")
    public String Home(){
        return "Home";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
