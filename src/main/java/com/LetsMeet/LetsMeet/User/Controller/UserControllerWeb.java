package com.LetsMeet.LetsMeet.User.Controller;

import com.LetsMeet.LetsMeet.RequestHandler;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Model.User_Internal;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.Models.UserData;
import com.LetsMeet.Models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserControllerWeb {

    @Autowired
    UserService userServiceInterface;

    @Autowired
    ValidationService userValidation;

    @RequestMapping("/Home")
    public String Home(Model model) {
        return "Home";
    }

    @GetMapping("/createevent")
    public String createevent(Model model) {
        model.addAttribute("event", new RequestHandler());
        return "createevent";
    }

    @GetMapping("/saveevent")
    public String saveevent(@RequestParam(name = "eventname") String eventname, @RequestParam(name = "eventdesc") String eventdesc, @RequestParam(name = "eventlocation") String eventlocation, Model model) {
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

        userServiceInterface.createUser(userfirstname, userlastname, useremail, userpassword);

        return "saveuser";
    }

    @PostMapping("/User")
    public void CreateUser(@RequestParam(value = "email") String email) {

    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String attemptlogin(@RequestParam(name = "loginemail") String email, @RequestParam(name = "loginpassword") String password, RedirectAttributes redirectAttributes) {

        User_Internal user = userValidation.validate(email, password);
        if(user != null) {
            redirectAttributes.addFlashAttribute("success", "You have logged in as " + user.getfName() + " " + user.getlName());
            return "redirect:/Home";
        }else{
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

        return "adminviewallusers";

    }
}