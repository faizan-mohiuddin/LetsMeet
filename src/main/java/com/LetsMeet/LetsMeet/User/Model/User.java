package com.LetsMeet.LetsMeet.User.Model;

public class User {

    String userUUID;
    String fName;
    String lName;
    String email;
    String salt;
    String passwordHash;

    public User(String UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.userUUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public String getUUID(){
        return this.userUUID;
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
