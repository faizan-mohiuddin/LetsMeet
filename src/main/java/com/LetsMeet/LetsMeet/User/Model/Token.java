package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

public class Token {

    String token;
    UUID user;
    long expires;

    public Token(String token, UUID user, long expires){
        this.token = token;
        this.user = user;
        this.expires = expires;
    }

    public String getToken(){
        return this.token;
    }

    public UUID getUserUUID(){
        return this.user;
    }

    public long getExpires(){
        return this.expires;
    }
}
