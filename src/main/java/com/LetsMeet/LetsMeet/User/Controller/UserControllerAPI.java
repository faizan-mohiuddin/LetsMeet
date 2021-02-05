package com.LetsMeet.LetsMeet.User.Controller;
import com.LetsMeet.LetsMeet.User.Service.*;
import com.LetsMeet.LetsMeet.User.Model.*;

import com.LetsMeet.LetsMeet.RequestHandler;
import com.LetsMeet.LetsMeet.UserManager;
import com.LetsMeet.Models.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControllerAPI {

    @Autowired
    UserService userServiceInterface;

    ValidationService userValidation = new ValidationService();

    @PostMapping("/api/User")
    public String API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                              @RequestParam(value="email") String email, @RequestParam(value="password") String password){
        return userServiceInterface.createUser(fName, lName, email, password);
    }

    // User login
    @PostMapping("/api/login")
    public String API_Login(@RequestParam(value="email") String email, @RequestParam(value="password") String password){
        User_Internal user = userValidation.validate(email, password);
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
            User_Internal user = userValidation.getUserFromToken(token);

            // Delete user
            return userServiceInterface.deleteUser(user);
        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }

    // Get a users data
    @GetMapping("/api/User")
    public User API_GetUser(@RequestParam(value="Token") String token){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            User_Internal user = userValidation.getUserFromToken(token);
            return user.convertToUser();
        }else{
            String errorText = (String) response[1];
            User errorData = new User(errorText, errorText, errorText);
            return errorData;
        }
    }
}
