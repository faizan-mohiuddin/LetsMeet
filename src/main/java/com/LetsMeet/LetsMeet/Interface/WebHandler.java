package com.LetsMeet.LetsMeet.Interface;

import com.LetsMeet.LetsMeet.RequestHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.LetsMeet.Models.Data.UserData;
import com.LetsMeet.Models.Connectors.UserModel;
import org.thymeleaf.model.IAttribute;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("userlogin") //add parameters wanted for session
public class WebHandler {

    @RequestMapping(value = "/")
    public String redirect() {
        return "redirect:Home";
    }

    @RequestMapping("/Home")
    public String Home(Model model, HttpSession session){

        UserData user = (UserData) session.getAttribute("userlogin");

        model.addAttribute("user", user);

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
        model.addAttribute("createuser", new UserModel());
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
    public String login(HttpSession session, RedirectAttributes redirectAttributes) {

        UserData user = (UserData) session.getAttribute("userlogin");

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
    public String attemptlogin(@RequestParam(name = "loginemail") String username, @RequestParam(name = "loginpassword") String password, RedirectAttributes redirectAttributes, Model model) {

        UserData login = RequestHandler.validate(username, password);

        try {
            if (login != null) {
                model.addAttribute("userlogin", login);
                redirectAttributes.addFlashAttribute("success", "You have logged in as " + login.getEmail());
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
    public String adminviewallevents(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        UserData user = (UserData) session.getAttribute("userlogin");

        if (user == null || !user.whatsUUID().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) { // this user UUID is the admin account's UUID

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("events", RequestHandler.getAllEvents());

            return "adminviewallevents";

        }
    }

    @GetMapping("/adminviewallusers")
    public String adminviewallusers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        UserData user = (UserData) session.getAttribute("userlogin");

        if (user == null || !user.whatsUUID().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) { // this user UUID is the admin account's UUID

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("users", RequestHandler.getAllUsers());

            return "adminviewallusers";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        UserData user = (UserData) session.getAttribute("userlogin");

        if (user == null) {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("myEvents", RequestHandler.getMyEvents(user.whatsUUID()));

            return "dashboard";

        }
    }

}
