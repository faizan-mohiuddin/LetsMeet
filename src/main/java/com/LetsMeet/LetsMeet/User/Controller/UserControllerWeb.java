package com.LetsMeet.LetsMeet.User.Controller;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerWeb {
    @Autowired
    UserService userServiceInterface;

    @RequestMapping(value = "/api/v1/user/users")
    public ResponseEntity<Object> getUsers(){
        System.out.println("hey!");
        return new ResponseEntity<>(userServiceInterface.getUsers(),HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/user/{uuid}")
    public ResponseEntity<Object> updateUser(@PathVariable("uuid") String uuid, @RequestBody User user){
        userServiceInterface.updateUser(uuid, user);
        return new ResponseEntity<>("User updated succesfully",HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/user/{uuid}")
    public ResponseEntity<Object> deleteUser(@PathVariable("uuid") String uuid){
        userServiceInterface.deleteUser(uuid);
        return new ResponseEntity<>("User deleted succesfully",HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/user")
    public ResponseEntity<Object> createUser(@RequestBody User user){
        userServiceInterface.createUser(user);

        return new ResponseEntity<>("User created succesfully", HttpStatus.OK);
    }


}
