package com.LetsMeet.LetsMeet.TestingTools;

public class TestingUsers {
    public String UUID;
    public String fName;
    public String lName;
    public String email;
    public String password;
    public String passwordHash;
    public String salt;
    public String token;

    public TestingUsers(String fName, String lName, String email, String password){
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
    }
}
