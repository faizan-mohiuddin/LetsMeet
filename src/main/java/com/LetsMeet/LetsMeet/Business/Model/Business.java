package com.LetsMeet.LetsMeet.Business.Model;

import java.util.UUID;

public class Business {

    private UUID uuid;
    private String name;

    public Business(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }
}
