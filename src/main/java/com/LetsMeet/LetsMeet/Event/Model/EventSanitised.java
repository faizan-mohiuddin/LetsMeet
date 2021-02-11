package com.LetsMeet.LetsMeet.Event.Model;

public class EventSanitised {
    public String name;
    public String description;
    public String location;

    public EventSanitised(String name, String desc, String location){
        this.name = name;
        this.description = desc;
        this.location = location;
    }
}
