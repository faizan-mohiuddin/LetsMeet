package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

public class Token {

    UUID uuid;
    UUID user;
    int expires;

    public Token(UUID uuid, UUID user, int expires){
        this.uuid = uuid;
        this.user = user;
        this.expires = expires;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public UUID getUserUUID(){
        return this.user;
    }

    public int getExpires(){
        return this.expires;
    }
}
