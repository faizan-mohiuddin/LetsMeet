package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

public class Token {

    UUID uuid;
    UUID user;
    long expires;

    public Token(UUID uuid, UUID user, long expires){
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

    public long getExpires(){
        return this.expires;
    }
}
