package com.LetsMeet.LetsMeet.User.Controller;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String attemptlogin(@RequestParam(name = "loginemail") String email, @RequestParam(name = "loginpassword") String password, RedirectAttributes redirectAttributes) {

        User user = userValidation.validate(email, password);
        if(user != null) {
            redirectAttributes.addFlashAttribute("success", "You have logged in as " + user.getfName() + " " + user.getlName());
            return "redirect:/Home";
        }else{
            redirectAttributes.addFlashAttribute("error", "There was a problem logging in!");
            return "redirect:/login";
        }

    }
}
