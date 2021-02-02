package com.LetsMeet.Models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({ "name", "description", "location"})
public class AdminEventData {

    UUID uuid;
    String name;
    String desc;
    String location;

    public AdminEventData(String uuid, String name, String desc, String location){
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
