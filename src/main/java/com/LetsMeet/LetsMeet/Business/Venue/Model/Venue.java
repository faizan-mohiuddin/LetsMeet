package com.LetsMeet.LetsMeet.Business.Venue.Model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Venue {
    UUID venueUUID;
    String name;
    List<String> facilities = new ArrayList<>();

    public Venue(UUID uuid, String name){
        this.venueUUID = uuid;
        this.name = name;
    }

    public Venue(String uuid, String name){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;
    }

    public UUID getUUID(){
        return this.venueUUID;
    }

    public String getName(){
        return this.name;
    }

    public void addFacility(String f){
        this.facilities.add(f);
    }

    public String getJsonFacilities(){
        String json = new Gson().toJson(this.facilities);
        return json;
    }

    public void setName(String name){
        if(!name.equals("")){
            this.name = name;
        }
    }

    public VenueSanitised convertToSanitised(){
        return new VenueSanitised(this.name, this.facilities);
    }
}
