package com.LetsMeet.LetsMeet.Business.Venue.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class VenueSanitised {
    String Name;
    List<String> Facilities = new ArrayList<>();

    public VenueSanitised(String name, List<String> facilities){
        this.Name = name;
        this.Facilities = facilities;
    }

    @JsonProperty("Name")
    public String getName(){
        return this.Name;
    }

    @JsonProperty("Facilities")
    public List<String> getFacilities(){
        return this.Facilities;
    }
}
