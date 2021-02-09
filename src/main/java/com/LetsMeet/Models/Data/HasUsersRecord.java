package com.LetsMeet.Models.Data;

public class HasUsersRecord {
    public String EventUUID;
    public String UserUUID;
    public boolean IsOwner;

    public void populate(String EventUUID, String UserUUID, boolean IsOwner){
        this.EventUUID = EventUUID;
        this.UserUUID = UserUUID;
        this.IsOwner = IsOwner;
    }
}
