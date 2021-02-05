package com.LetsMeet.LetsMeet.User.Controller;
import com.LetsMeet.LetsMeet.User.Service.*;
import com.LetsMeet.LetsMeet.User.Model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControllerAPI {

    @Autowired
    UserService userServiceInterface;

    @Autowired
    ValidationService userValidation;

    @PostMapping("/api/User")
    public String API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                              @RequestParam(value="email") String email, @RequestParam(value="password") String password){
        System.out.println("Hiiiiiiiii");
        return userServiceInterface.createUser(fName, lName, email, password);
    }

    @RequestMapping("/api/test")
    public String API_Test(){
        userServiceInterface.getUsers();
        return ("hello");
    }

    // User login
    @PostMapping("/api/login")
    public String API_Login(@RequestParam(value="email") String email, @RequestParam(value="password") String password){
        User user = userValidation.validate(email, password);
        if(user != null) {
            // If true API token is returned
            return userServiceInterface.getUserToken(user);
        }else{
            return "Error, invalid email or password";
        }
    }

    // Delete user account
    @DeleteMapping("/api/User")
    public String API_DeleteUser(@RequestParam(value="Token") String token){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);

            // Delete user
            return userServiceInterface.deleteUser(user);
        }else{
            return (String) response[1];
        }
    }

    // Get a users data
    @GetMapping("/api/User")
    public UserSanitised API_GetUser(@RequestParam(value="Token") String token){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            User user = userValidation.getUserFromToken(token);
            return userServiceInterface.getSantitised(user);

        }else{
            String errorText = (String) response[1];
            return new UserSanitised(errorText, errorText, errorText);
        }
    }
}
