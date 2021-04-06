package com.LetsMeet.LetsMeet.Business.Model;

import com.LetsMeet.LetsMeet.Venue.Model.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Business {

    private UUID uuid;
    private String name;
    private List<Venue> venues = new ArrayList<>();

    public Business(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public List<Venue> getVenues(){
        return this.venues;
    }

    public void setVenues(List<Venue> v){
        this.venues = v;
    }

    public void setName(String name){
        if(!name.equals("")){
            this.name = name;
        }
    }
}
