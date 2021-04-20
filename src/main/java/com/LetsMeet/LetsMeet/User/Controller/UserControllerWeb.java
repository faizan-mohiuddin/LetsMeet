package com.LetsMeet.LetsMeet.User.Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("userlogin")
public class UserControllerWeb {

    @Autowired
    UserService userServiceInterface;

    @Autowired
    ValidationService userValidation;

    @Autowired
    EventService eventService;

    @Autowired
    EventResponseService eventResponseService;
    
    @Autowired
    BusinessService businessService;

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


    @GetMapping({"/createuser", "/register"})
    public String createuser(Model model, HttpSession session, RedirectAttributes redirectAttributes,
                             @RequestParam(value="email", defaultValue = "") String guestEmail) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {

            model.addAttribute("user", user);

            if(!guestEmail.equals("")){
                model.addAttribute("GivenGuestEmail", guestEmail);
            }else{
                model.addAttribute("GivenGuestEmail", null);
            }

            return "createuser";

        } else {

            redirectAttributes.addFlashAttribute("info", "You are already logged in.");

            return "redirect:/Home";

        }

    }

    @GetMapping("/saveuser")
    public String saveuser(@RequestParam(name = "userfirstname") String userfirstname,
                           @RequestParam(name = "userlastname") String userlastname,
                           @RequestParam(name = "useremail") String useremail,
                           @RequestParam(name = "userpassword") String userpassword,
                           Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {
            if (!userServiceInterface.isValidRegister(userfirstname, userlastname, useremail, userpassword)) {

                redirectAttributes.addFlashAttribute("danger", "There was a problem registering this account.");

                return "redirect:/createuser";

            } else {

                model.addAttribute("userfirstname", userfirstname);
                model.addAttribute("userlastname", userlastname);
                model.addAttribute("useremail", useremail);
                model.addAttribute("userpassword", userpassword);

                String response = userServiceInterface.createUser(userfirstname, userlastname, useremail, userpassword);

                this.loginToSession(useremail, userpassword, model, session);

                redirectAttributes.addFlashAttribute("notice", response);
                return "saveuser";
            }
        } else {

            redirectAttributes.addFlashAttribute("info", "You are already logged in.");

            return "redirect:/Home";

        }
    }


    @GetMapping("/login")
    public String login(HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if (user != null) {

            redirectAttributes.addFlashAttribute("info", "You are already logged in.");

            return "redirect:/Home";

        } else {

            return "login";

        }
    }

    @GetMapping("/logout")
    public String logout(SessionStatus session, RedirectAttributes redirectAttributes, HttpSession usersession) {

        User user = (User) usersession.getAttribute("userlogin");

        if (user == null) {

            redirectAttributes.addFlashAttribute("warning", "You are not logged in.");

        } else {

            redirectAttributes.addFlashAttribute("success", "You have successfully logged out.");
            session.setComplete();

        }
        return "redirect:/Home";

    }

    @PostMapping("/login")
    public String attemptlogin(@RequestParam(name = "loginemail") String email, @RequestParam(name = "loginpassword") String password,
                               RedirectAttributes redirectAttributes, Model model, HttpSession session) {

        User user = userValidation.validate(email, password);
        if(user != null) {
            model.addAttribute("userlogin", user);
            session.setAttribute("apiToken", userServiceInterface.getUserToken(user));
            redirectAttributes.addFlashAttribute("success", "You have logged in as " + user.getfName() + " " + user.getlName());
            return "redirect:/Home";
        }else{
            redirectAttributes.addFlashAttribute("error", "There was a problem logging in!");
            return "redirect:/login";
        }

    }

    private void loginToSession(String email, String password, Model model, HttpSession session){
        User user = userValidation.validate(email, password);
        if(user != null) {
            model.addAttribute("userlogin", user);
            model.addAttribute("user", user);
            session.setAttribute("apiToken", userServiceInterface.getUserToken(user));
        }
    }

    @GetMapping("/adminviewallusers")
    public String adminviewallusers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");


        if (user == null || !user.getIsAdmin()) { // checks if logged in user is an admin

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("user", user);

            model.addAttribute("users", userServiceInterface.getUsers());

            return "adminviewallusers";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("Received");

        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        }



       var events = eventService.getUserEvents(user);
        model.addAttribute("events", events);

        var responses = eventResponseService.getResponsesWithEvent(user);
        model.addAttribute("responses", responses);

        return "dashboard";
    }

    @GetMapping("/deleteuser/{useruuid}")
    public String deleteUser(@PathVariable("useruuid") String useruuid, Model model, RedirectAttributes redirectAttributes, HttpSession session,
                             SessionStatus endSession) {

        User user = (User) session.getAttribute("userlogin");

        // User must be logged in and user must be admin in order to delete accounts.
        // TODO Re-do this function so that a user can delete their own account once a 'settings' page is created.

        // Check user has permission to delete this account
        if (!(user != null && (user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db") || user.getUUID().toString().equals(useruuid)))) {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to execute this action.");

            return "redirect:/Home";

        } else {

            String destination;
            if(user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")){
                destination = "redirect:/adminviewallusers";
            }else{
                destination = "redirect:/Home";
            }

            User userToDelete = userServiceInterface.getUserByUUID(useruuid);

            if (userToDelete == null) {

                redirectAttributes.addFlashAttribute("danger", "There was an error finding the user.");

                return destination;

            } else {

                String tryDeleteUser = userServiceInterface.deleteUser(userToDelete);

                if (tryDeleteUser.equals("User successfully deleted.")) {

                    redirectAttributes.addFlashAttribute("success", "User deleted successfully.");

                    // Log out of session
                    endSession.setComplete();

                } else {

                    redirectAttributes.addFlashAttribute("danger", "There was an error deleting the user.");

                }

            }

            return destination;

        }
    }

    @GetMapping("/edituser/{useruuid}")
    public String editUser(@PathVariable("useruuid") String useruuid, RedirectAttributes redirectAttributes, HttpSession session, Model model) {

        User user = (User) session.getAttribute("userlogin");

        // User must be logged in and user must be admin in order to edit accounts.
        // TODO Re-do this function so that a user can edit their own account once a 'settings' page is created.
        // NB: editing only allows the user to edit their FName, LName and E-Mail. Will try password editing soon.

        if (!(user != null && (user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db") || user.getUUID().toString().equals(useruuid)))) {

            redirectAttributes.addFlashAttribute("accessDenied" ,"You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("user", user);
            model.addAttribute("editingUser", userServiceInterface.getUserByUUID(useruuid));

            return "edituser";

        }
    }

    @PostMapping("/updateuser/{useruuid}")
    public String updateUser(@PathVariable("useruuid") String useruuid,
                             @RequestParam(name = "userfirstname", defaultValue="") String firstName,
                             @RequestParam(name = "userlastname", defaultValue="") String lastName,
                             @RequestParam(name = "useremail", defaultValue="") String email,
                             @RequestParam(name = "userpassword", defaultValue="") String pw,
                             HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if(!(user != null && (user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db") || user.getUUID().toString().equals(useruuid)))) {

            redirectAttributes.addFlashAttribute("accessDenied" ,"You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            if (!userServiceInterface.isValidUpdate(firstName, lastName, email)) {

                redirectAttributes.addFlashAttribute("registerFailed", "There was a problem editing this account. Please check the credentials to see if they are valid.");

                return "redirect:/edituser/" + useruuid;

            }else{

                User userToUpdate = userServiceInterface.getUserByUUID(useruuid);

                String tryUpdateUser = userServiceInterface.updateUser(userToUpdate, firstName, lastName, email, pw, user.getIsGuest());

                if (tryUpdateUser.equals("User successfully updated")) {

                    redirectAttributes.addFlashAttribute("success", "User successfully updated.");

                } else {

                    redirectAttributes.addFlashAttribute("danger", tryUpdateUser);

                }

                // Check if admin or user
                if(user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) {
                    return "redirect:/adminviewallusers";
                }else{
                    return "redirect:/myaccount";
                }

            }

        }

    }

    @GetMapping("/myaccount")
    public String MyAccountPage(Model model, RedirectAttributes redirectAttributes, HttpSession session){
        // Check user is logged in
        User user = (User) session.getAttribute("userlogin");

        if(user == null){
            return "redirect:/404";
        }
        model.addAttribute("user", user);

        // Delete account URL
        String deletePath = String.format("/deleteuser/%s", user.getUUID().toString());
        model.addAttribute("deletePath", deletePath);

        // Edit account URL
        String updatePath = String.format("/edituser/%s", user.getUUID().toString());
        model.addAttribute("editPath", updatePath);

        // Businesses
        Collection<Business> businesses = businessService.getUserBusinesses(user.getUUID().toString());
        model.addAttribute("businesses", businesses);

        return "User/MyAccount";
    }

    // Error catching
    @ExceptionHandler(Exception.class)
    public String handleException(){
        return "redirect:/405";
    }
}
