package com.LetsMeet.LetsMeet.Event.Model;

import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;

public class Events {

    private Events() {
        throw new IllegalStateException("Utility class");
      }
    
    public static Event from(EventDTO eventDTO){
        Event event = new Event(eventDTO.getName());
        event.setLocation(eventDTO.getLocation());
        event.setDescription(eventDTO.getDescription());
        event.getEventProperties().setFacilities(eventDTO.getFacilities());

        List<DateTimeRange> times = new ArrayList<>();
            for (var time : eventDTO.getTimes())
                times.add(DateTimeRange.fromJson(time));
        event.getEventProperties().setTimes(times);

        event.getEventProperties().setLocation(new Location(eventDTO.getName(), eventDTO.getLatitude(), eventDTO.getLongitude(), eventDTO.getRadius()));
        //TODO event.setProperties(properties)
        return event;
    }
}

