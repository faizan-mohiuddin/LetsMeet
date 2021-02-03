package com.LetsMeet.Models.Data;

public class TokenData {
    String UserUUID;
    String Token;
    int Expires;

    public void populate(String uuid, String token, int expires){
        this.UserUUID = uuid;
        this.Token = token;
        this.Expires = expires;
    }

    public String getUserUUID(){
        return this.UserUUID;
    }

    public String getToken(){
        return this.Token;
    }

    public int getExpires(){
        return this.Expires;
    }
}
