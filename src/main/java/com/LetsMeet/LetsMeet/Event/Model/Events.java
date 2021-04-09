package com.LetsMeet.LetsMeet.Event.Model;

import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;

public class Events {
    
    public static Event from(EventDTO eventDTO){
        Event event = new Event(eventDTO.getName());
        event.setLocation(eventDTO.getLocation());
        event.setDescription(eventDTO.getLocation());
        event.getEventProperties().setFacilities(eventDTO.getFacilities());
        //TODO event.setProperties(properties);
        return event;
    }
}

