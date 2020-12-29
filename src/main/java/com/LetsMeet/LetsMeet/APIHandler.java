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
}
