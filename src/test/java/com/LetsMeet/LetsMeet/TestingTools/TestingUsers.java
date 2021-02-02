package com.LetsMeet.LetsMeet.TestingTools;

import com.LetsMeet.LetsMeet.APIHandler;

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

    private APIHandler controller = new APIHandler();

    public TestingUsers(String fName, String lName, String email, String password){
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
    }

    public void login(){
        this.token = this.controller.API_Login(this.email, this.password);
    }
}
