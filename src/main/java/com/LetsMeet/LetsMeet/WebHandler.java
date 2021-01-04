package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

import com.LetsMeet.Models.UserModel;

@Controller
public class WebHandler {

    @RequestMapping("/Home")
    public String Home(Model model){
        UserModel user1 = new UserModel();
        user1.setUsername("faizan");
        user1.setPassword("chicken123");
        model.addAttribute("num", user1.getUsername());
        return "Home";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
