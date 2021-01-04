package com.LetsMeet.Models;

public class UserData {

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
}
