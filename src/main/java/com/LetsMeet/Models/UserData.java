package com.LetsMeet.Models;

public class UserData {

    String UserUUID;
    String fName;
    String lName;
    String email;
    String passwordHash;
    String salt;

    String username;
    String password;

    public String getUsername(){

        return username;

    }

    public void setUsername(String newUserName) {

        this.username = newUserName;

    }

    public String getPassword() {

        return password;

    }

    public void setPassword(String newPassWord){

        this.password = newPassWord;
        
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public void populate(String UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.UserUUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getPasswordHash(){
        return this.passwordHash;
    }

    public String getSalt(){
        return this.salt;
    }

}
