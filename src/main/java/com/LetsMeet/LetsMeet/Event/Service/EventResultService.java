package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.DAO.*;
import com.LetsMeet.LetsMeet.Event.Model.*;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventResultService {

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventResultService.class);
    
    @Autowired
    EventResultDao resultDao;

    @Autowired
    EventService eventService;

    @Autowired
    EventResponseService responseService;


    public EventResult newEventResult(Event event) throws IllegalArgumentException{
        try{
        EventResult result = new EventResult(event.getUUID());
        resultDao.save(result);
        return result;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Event Result already exists");
        }
    }

    public EventResult calculateResults(Event event, User user, int duration, boolean requiredUsers) throws IllegalArgumentException{
        try{
            calculateTimes(event, duration, requiredUsers);
            calculateLocation(event,1,true);
            
            return resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate results: " + e.getMessage());
        }
    }

    public EventResult calculateTimes(Event event, int duration, boolean requiredUsers) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
            List<EventResponse> responses = responseService.getResponses(event);
            EventTimeSolver timeSolver = new EventTimeSolver(eventService.getEvent(event.getUUID().toString()), responses);


            List<EventResponse> requiredResponses = new ArrayList<>();
            for (EventResponse response : responses){
                if (response.getRequired().booleanValue()) requiredResponses.add(response);
            }

            timeSolver.solve(1);

            if(duration > 4) timeSolver.withDuration(Duration.ofMinutes(duration));
            if(requiredUsers) timeSolver.withResponses(requiredResponses);

            result.setUniqueResponses(responses.size());
            result.getDates().setGradedProperties(timeSolver.getSolution());
            resultDao.update(result);
            return result;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate times: " + e.getMessage() + " Note: Possible deserialisation failure");
        }
        
    }

    public void selectTimes(Event event, int timeIndex) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseThrow();
            var selected = result.getDates().getGradedProperties().get(timeIndex);
            result.getDates().setSelected(selected);
        }
        catch(IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Could not select times: Selected time is out of range");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not select times: " + e.getMessage());
        }
    }

    public EventResult calculateLocation(Event event, int minOpt, boolean requiredUsers) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
            List<EventResponse> responses = responseService.getResponses(event);
            EventLocationSolver locationSolver = new EventLocationSolver(eventService.getEvent(event.getUUID().toString()).getEventProperties(), responses);
            
            result.getLocations().setGradedProperties(locationSolver.solve());
            resultDao.update(result);
            return result;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate location: " + e.getMessage());
        }
    }
}
