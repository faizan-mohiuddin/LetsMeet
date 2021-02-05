package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

// ONLY FOR INTERNAL USE. PASSWORD MAY BE STORED IN PLAIN TEXT WHEN CREATING NEW USER
public class User {

    UUID UUID;
    String fName;
    String lName;
    String email;
    String passwordHash;
    String salt;

    public User(UUID UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.UUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    // Let's not have mutliple constructors forr different uuid types.
    /*
    public User(String UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.UUID = java.util.UUID.fromString(UUID);
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }
    */

    public UserSanitised convertToUser(){
        return new UserSanitised(this.fName, this.lName, this.email);
    }

    public String getPasswordHash(){
        return this.passwordHash;
    }

    public String getSalt(){
        return this.salt;
    }

    public String getfName(){
        return this.fName;
    }

    public String getlName(){
        return this.lName;
    }

    public String getEmail(){
        return this.email;
    }

    public String getStringUUID(){
        return this.UUID.toString();
    }
}
