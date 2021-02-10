package com.LetsMeet.LetsMeet.User.Controller;

import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Event.Service.EventServiceInterface;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.UserServiceInterface;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("userlogin")
public class UserControllerWeb {

    @Autowired
    UserService userServiceInterface;

    @Autowired
    ValidationService userValidation;

    @Autowired
    EventService eventServiceInterface;

    @RequestMapping(value = "/")
    public String redirect() {
        return "redirect:Home";
    }

    @RequestMapping("/Home")
    public String Home(Model model, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        model.addAttribute("user", user);

        return "Home";

    }


    @GetMapping("/createuser")
    public String createuser(Model model) {
        model.addAttribute("user", new UserService());
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
        // Why is this method empty? How does this still work? #include PureMagic? Probably
    }

    @GetMapping("/login")
    public String login(HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if (user != null) {

            redirectAttributes.addFlashAttribute("alreadyLoggedIn", "You are already logged in.");

            return "redirect:/Home";

        } else {

            return "login";

        }
    }

    @GetMapping("/logout")
    public String logout(SessionStatus session, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("loggedout", "You have successfully logged out.");
        session.setComplete();

        return "redirect:/Home";

    }

    @PostMapping("/login")
    public String attemptlogin(@RequestParam(name = "loginemail") String email, @RequestParam(name = "loginpassword") String password, RedirectAttributes redirectAttributes, Model model) {

        User user = userValidation.validate(email, password);
        if(user != null) {
            model.addAttribute("userlogin", user);
            redirectAttributes.addFlashAttribute("success", "You have logged in as " + user.getfName() + " " + user.getlName());
            return "redirect:/Home";
        }else{
            redirectAttributes.addFlashAttribute("error", "There was a problem logging in!");
            return "redirect:/login";
        }

    }

    @GetMapping("/adminviewallusers")
    public String adminviewallusers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null || !user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) { // this user UUID is the admin account's UUID

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("users", userServiceInterface.getUsers());

            return "adminviewallusers";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            if (eventServiceInterface.getUserEvents(user.getUUID().toString()).isEmpty()) {

                Boolean noEvents = true;
                model.addAttribute("noEvents", noEvents);

            } else {

                model.addAttribute("myEvents", eventServiceInterface.getUserEvents(user.getUUID().toString()));

            }
            return "dashboard";

        }
    }
}
