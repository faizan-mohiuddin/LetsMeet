package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.LetsMeet.Models.UserData;
import com.LetsMeet.Models.UserModel;

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

    @GetMapping("/createevent")
    public String createevent(Model model) {
        model.addAttribute("event", new EventHandler());
        return "createevent";
    }

    @GetMapping("/saveevent")
    public String saveevent(@RequestParam(name = "eventname") String eventname, @RequestParam(name = "eventdesc") String eventdesc, @RequestParam(name = "eventlocation") String eventlocation, Model model){
        model.addAttribute("eventname", eventname);
        model.addAttribute("eventdesc", eventdesc);
        model.addAttribute("eventlocation", eventlocation);
        EventHandler.createEvent(eventname, eventdesc, eventlocation);
        return "saveevent";
    }

    @GetMapping("/createuser")
    public String createuser(Model model) {
        model.addAttribute("user", new UserModel());
        return "createuser";
    }

    @GetMapping("/saveuser")
    public String saveuser(@RequestParam(name = "userfirstname") String userfirstname, @RequestParam(name = "userlastname") String userlastname, @RequestParam(name = "useremail") String useremail, @RequestParam(name = "userpassword") String userpassword, Model model) {
        model.addAttribute("userfirstname", userfirstname);
        model.addAttribute("userlastname", userlastname);
        model.addAttribute("useremail", useremail);
        model.addAttribute("userpassword", userpassword);

        EventHandler.createUser(userfirstname, userlastname, useremail, userpassword);

        return "saveuser";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
