package com.LetsMeet.Models;

import java.util.UUID;

public class EventData {

    UUID uuid;
    String name;
    String desc;
    String location;

    public EventData(String uuid, String name, String desc, String location){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.desc = desc;
        this.location = location;
    }

    // NECESSARY METHODS FOR RETURNING DATA TO API - DO NOT TOUCH
    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.desc;
    }

    public String getLocation(){
        return this.location;
    }
    // END OF NECESSARY METHODS

}
