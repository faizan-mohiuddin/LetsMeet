package com.LetsMeet.LetsMeet.Event.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Events;
import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventCoordinator {
    
    @Autowired
    private EventService eventService;

    @Autowired 
    private PollService pollService;

    public void updateEvent(EventDTO eventDTO, User user){
        try{

        // Event must already exist, try to load it and throw exception if not found
        Event event = eventService.get(UUID.fromString(eventDTO.getUUID())).orElseThrow();

        // Update the basics (name, description, image, generic properties)
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());

        
        /*  Update EventProperties  */
        // Location
        event.getEventProperties().setLocation(new Location(
            eventDTO.getLocation(),
            eventDTO.getLatitude(),
            eventDTO.getLongitude(), 
            eventDTO.getRadius()));
        
        // Times
        List<DateTimeRange> times = new ArrayList<>();
        for (var time : eventDTO.getTimes())
            times.add(DateTimeRange.fromJson(time));
         
        event.getEventProperties().setTimes(times);

        // Services
        event.getEventProperties().setFacilities(eventDTO.getFacilities());
        

        // Check and update polls

        // Send modified event to update
        eventService.update(user, event);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        
    }

    public Event createEvent(EventDTO eventDTO){
        return Events.from(eventDTO);
    }
}
