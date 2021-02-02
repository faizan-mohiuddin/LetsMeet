package com.LetsMeet.Models;

public class AdminUserData {

    String UserUUID;
    String fName;
    String lName;
    String email;
    String salt;
    String passwordHash;

    public void populate(String UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.UserUUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public String getUserUUID(){
        return this.UserUUID;
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

    public String getSalt(){
        return this.salt;
    }

    public String getPasswordHash(){
        return this.passwordHash;
    }
}
