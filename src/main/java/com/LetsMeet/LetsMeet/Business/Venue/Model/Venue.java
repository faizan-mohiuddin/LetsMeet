package com.LetsMeet.LetsMeet.Business.Venue.Model;

import java.util.UUID;

public class Venue {
    UUID venueUUID;
    String name;

    public Venue(UUID uuid, String name){
        this.venueUUID = uuid;
        this.name = name;
    }
}
