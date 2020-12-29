package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class WebHandler {

    @RequestMapping("/Home")
    public String Home(Model model){
        int num = 56;
        model.addAttribute("num", num);
        return "Home";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
