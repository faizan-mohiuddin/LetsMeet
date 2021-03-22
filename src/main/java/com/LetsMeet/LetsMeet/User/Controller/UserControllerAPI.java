package com.LetsMeet.LetsMeet.User.Controller;

import com.LetsMeet.LetsMeet.User.Service.*;

import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.User.Model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControllerAPI {

    @Autowired
    UserService userServiceInterface;

    @Autowired
    ValidationService userValidation;

    // Post Mappings
    //-----------------------------------------------------------------
    @PostMapping("/api/User")
    public String API_AddUser(@RequestParam(value="fName") String fName, @RequestParam(value="lName") String lName,
                              @RequestParam(value="email") String email, @RequestParam(value="password") String password){
        return userServiceInterface.createUser(fName, lName, email, password);
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
    

    // Get Mappings
    //-----------------------------------------------------------------

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

    @GetMapping("/api/User/token")
    public String getToken(HttpSession session){
        try{
            User user = (User) session.getAttribute("userlogin");
            return userServiceInterface.getUserToken(user);
        }
        catch(Exception e){
            return "nothing"; //TODO logger
        }
    }

    // Delete Mappings
    //-----------------------------------------------------------------

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

    // Put Mappings
    //-----------------------------------------------------------------
    @PutMapping("/api/User")
    public String API_UpdateUser(@RequestParam(value="Token", defaultValue="") String token,
                                 @RequestParam(value="FName", defaultValue="") String fName,
                                 @RequestParam(value="LName", defaultValue="") String lName,
                                 @RequestParam(value="email", defaultValue="") String email){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);

            // Update user
            return userServiceInterface.updateUser(user, fName, lName, email);
        }else{
            return (String) response[1];
        }
    }

    @PutMapping("/api/User/Password")
    public String API_UpdateUserPassword(@RequestParam(value="Token", defaultValue="") String token,
                                 @RequestParam(value="CurrentPassword", defaultValue="") String currentPassword,
                                 @RequestParam(value="NewPassword", defaultValue="") String newPassword,
                                 @RequestParam(value="PasswordConfirmation", defaultValue="") String passwordConfirmation){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);

            // Update user
            return userServiceInterface.updateUserPassword(user, currentPassword, newPassword, passwordConfirmation);
        }else{
            return (String) response[1];
        }
    }

}
