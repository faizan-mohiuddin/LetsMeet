package com.LetsMeet.LetsMeet.User.Model;

public class UserSanitised {

    String fName;
    String lName;
    String email;

    public UserSanitised(String fName, String lName, String email){
        this.fName = fName;
        this.lName = lName;
        this.email = email;
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


}
