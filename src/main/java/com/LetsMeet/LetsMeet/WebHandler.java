package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.LetsMeet.Models.UserData;
import com.LetsMeet.Models.UserModel;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.LetsMeet.LetsMeet.UserManager;

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

        UserManager usrmngr = new UserManager();

        byte[] theSalt = usrmngr.generateSalt();
        byte[] theHash = usrmngr.generateHash(userpassword, theSalt);
        UUID theUUID = usrmngr.createUserUUID(userfirstname, userlastname, useremail);

        String saltString = usrmngr.toHex(theSalt);
        String hashString = usrmngr.toHex(theHash);

        UserModel usr = new UserModel();
        usr.newUser(theUUID.toString(), userfirstname, userlastname, useremail, hashString, saltString);

        return "saveuser";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }
}
