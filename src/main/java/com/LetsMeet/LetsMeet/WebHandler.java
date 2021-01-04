package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.LetsMeet.Models.UserData;

@Controller
public class WebHandler {

    @RequestMapping("/Home")
    public String Home(Model model){
        UserData user1 = new UserData();
        user1.setUsername("faizan");
        user1.setPassword("chicken123");
        model.addAttribute("num", user1.getUsername());
        return "Home";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
