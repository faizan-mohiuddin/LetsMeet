package com.LetsMeet.LetsMeet.TestingTools;

import com.LetsMeet.LetsMeet.User.Controller.UserControllerAPI;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


public class TestingUsers {
    public String UUID;
    public String fName;
    public String lName;
    public String email;
    public String password;
    public String passwordHash;
    public String salt;
    public String token;
    public ArrayList<String> events = new ArrayList<>();

    private final UserControllerAPI userController = new UserControllerAPI();

    public TestingUsers(String fName, String lName, String email, String password){
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
    }
}
