package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

public class User {

    UUID UUID;
    String fName;
    String lName;
    String email;
    String passwordHash;
    String salt;
    Boolean isAdmin = false;
    Boolean isGuest = false;

    public User(UUID UUID, String fName, String lName, String email, String passwordHash, String salt){
        this.UUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public User(UUID UUID, String fName, String lName, String email, String passwordHash, String salt, Boolean isAdmin){
        this.UUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    public User(UUID UUID, String fName, String lName, String email, String passwordHash, String salt, Boolean isAdmin, Boolean isGuest){
        this.UUID = UUID;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
        this.isGuest = isGuest;
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

    public Boolean getIsAdmin() { return this.isAdmin; }

    public void setIsAdmin(Boolean b) { this.isAdmin = b; }

    public Boolean getIsGuest(){
        return this.isGuest;
    }

    public int getIsGuestInt(){
        if(this.getIsGuest()){
            return 1;
        }
        return 0;
    }

    public void setIsGuest(Boolean b){
        this.isGuest = b;
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

    public void switchIsGuestStatus(boolean b){
        this.isGuest = b;
    }

    public void prepareUpdate(){
        // Store password hash and salt in object
        if(this.passwordHash == null){

        }

        if(this.salt == null){

        }
    }

    public void setfName(String fname){
        this.fName = fname;
    }

    public void setlName(String lName){
        this.lName = lName;
    }

    public void setPWHash(String hash){
        this.passwordHash = hash;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }
}
