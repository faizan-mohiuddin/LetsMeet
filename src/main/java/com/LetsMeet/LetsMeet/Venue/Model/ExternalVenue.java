package com.LetsMeet.LetsMeet.Venue.Model;

public class ExternalVenue {
    public String name;

    public ExternalVenue(String name){
        this.name = name.replaceAll("\"", "");
    }
}
