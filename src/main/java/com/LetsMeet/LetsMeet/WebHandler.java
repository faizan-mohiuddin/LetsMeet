package com.LetsMeet.LetsMeet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.UserData;
import com.LetsMeet.Models.UserModel;

@Controller
public class WebHandler {

    @RequestMapping("/Home")
    public String Home(Model model){
        return "Home";
    }

    @GetMapping("/createevent")
    public String createevent(Model model) {
        model.addAttribute("event", new RequestHandler());
        return "createevent";
    }

    @GetMapping("/saveevent")
    public String saveevent(@RequestParam(name = "eventname") String eventname, @RequestParam(name = "eventdesc") String eventdesc, @RequestParam(name = "eventlocation") String eventlocation, Model model){
        model.addAttribute("eventname", eventname);
        model.addAttribute("eventdesc", eventdesc);
        model.addAttribute("eventlocation", eventlocation);
        RequestHandler.createEvent(eventname, eventdesc, eventlocation, null);
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

        RequestHandler.createUser(userfirstname, userlastname, useremail, userpassword);

        return "saveuser";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email){

    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String attemptlogin(@RequestParam(name = "loginemail") String username, @RequestParam(name = "loginpassword") String password, RedirectAttributes redirectAttributes) {

        UserModel usr1 = new UserModel();

        UserData emailexists = usr1.getUserByEmail(username);

        try {
            if (emailexists != null) {
                redirectAttributes.addFlashAttribute("success", "You have logged in as " + emailexists.getEmail());
                return "redirect:/Home";
            } else {

                redirectAttributes.addFlashAttribute("error", "There was a problem logging in!");
                return "redirect:/login";
            }
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", "There was a problem logging in!");
            return "redirect:/login";

        }

    }

    @GetMapping("/adminviewallevents")
    public String adminviewallevents(Model model) {

        model.addAttribute("events", RequestHandler.getAllEvents());

        return "adminviewallevents";
    }

    @GetMapping("/adminviewallusers")
    public String adminviewallusers(Model model) {

        model.addAttribute("users", RequestHandler.getAllUsers());

        return"adminviewallusers";
        
    }
}
