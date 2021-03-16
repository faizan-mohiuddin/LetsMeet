package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

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

    public User(String uuid){
        this.UUID = UUID.fromString(uuid);
    }

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

    public UUID getUUID(){
        return this.UUID;
    }

    public String getStringUUID(){
        return this.UUID.toString();
    }

    public void hideSensitiveDetails(){
        this.passwordHash = null;
        this.salt = null;
    }

    public void switchFName(String fName){
        if(!fName.equals("")){
            this.fName = fName;
        }
    }

    public void switchLName(String lName){
        if(!lName.equals("")){
            this.lName = lName;
        }
    }

    public void switchEmail(String email){
        if(!email.equals("")){
            this.email = email;
        }
    }

    public void prepareUpdate(){
        // Store password hash and salt in object
        if(this.passwordHash == null){

        }

        if(this.salt == null){

        }
    }
}
