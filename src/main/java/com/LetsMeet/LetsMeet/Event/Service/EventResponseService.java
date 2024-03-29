//-----------------------------------------------------------------
// EventResponseService.java
// Let's Meet 2021
//
// Implements the operations possible on an EventResponse.

package com.LetsMeet.LetsMeet.Event.Service;

//-----------------------------------------------------------------
import com.LetsMeet.LetsMeet.Event.Model.DTO.ResponseDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import com.LetsMeet.LetsMeet.Event.DAO.EventResponseDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//-----------------------------------------------------------------

@Service
public class EventResponseService {
    
    // Components
    //-----------------------------------------------------------------

    // Data Access Object (database interface)
    @Autowired
    EventResponseDao dao;

    @Autowired
    PollService pollService;

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventResponseService.class);

    // Creation
    //-----------------------------------------------------------------
    public EventResponse createResponse(User user, Event event, Boolean required) throws IllegalArgumentException{
        try{
        EventResponse response = new EventResponse(event.getUUID(), user.getUUID(), EventProperties.getEmpty());
        response.setRequired(required);
        dao.save(response);
        return response;
        }        
        catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        
    }

    public boolean update(EventResponse response){
        try{
            return dao.update(response);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public boolean update(EventResponse response, ResponseDTO responseDTO){
        try{
            // Update location
            response.getEventProperties().setLocation( new Location(
                    responseDTO.getLocation(),
                    responseDTO.getLatitude(),
                    responseDTO.getLongitude(),
                    responseDTO.getRadius()
            ));

            // Update times
            List<DateTimeRange> times = new ArrayList<>();
            for (var time : responseDTO.getTimes())
                times.add(DateTimeRange.fromJson(time));

            response.getEventProperties().setTimes(times);

            // Update facilities
            response.getEventProperties().setFacilities(responseDTO.getFacilities());

            return update(response);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean deleteResponse(User user, Event event) throws IllegalArgumentException{
        try{
            EventResponse response = dao.get(event.getUUID(), user.getUUID()).orElseThrow();

            return dao.delete(response);

        }catch(NoSuchElementException e){
            throw new IllegalArgumentException("Resonse not found");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Unable to delete");
        }
    }

    public Boolean clearResponse(User user, Event event) throws IllegalArgumentException{
        try{
            EventResponse response = dao.get(event.getUUID(), user.getUUID()).orElseThrow();
            response.setEventProperties(EventProperties.getEmpty());
            return dao.update(response);

        }catch(NoSuchElementException e){
            throw new IllegalArgumentException("Resonse not found");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Unable to delete");
        }
    }

    public Optional<EventResponse> getResponse(User user, Event event){
        try{
            return Optional.of(dao.get(event.getUUID(), user.getUUID()).orElseThrow(IllegalArgumentException::new));
        }
        catch(Exception e){
            return Optional.empty();
        }
    }

    public List<EventResponse> getResponses(Event event){
        try{
            return dao.get(event.getUUID()).get();
        }
        catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<EventResponse> getResponses(User user){
        try{
            return dao.get(user.getUUID()).get();
        }
        catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Map<EventResponse, Event> getResponsesWithEvent(User user){
        try{
            return dao.getWithEvent(user.getUUID());
        }
        catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean saveResponse(EventResponse response){
        try{
            return dao.update(response);
        }
        catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // Modification
    //-----------------------------------------------------------------

    // set times
    public boolean setTimes(EventResponse response, List<DateTimeRange> times){
        try{
            response.getEventProperties().setTimes(times);
            dao.update(response);
            return true;
        }
        catch (Exception e){
            LOGGER.error("Failed to add response: {} ", e.getMessage());
            return false;
        }
    }

    // get times
    public Optional<List<DateTimeRange>> getTimes(EventResponse response){
        try{
            return Optional.of(response.getEventProperties().getTimes());
        }
        catch (Exception e){
            LOGGER.error("Failed to get times: {} ", e.getMessage());
            return Optional.empty();
        }
    }

    // clear times
    public boolean clearTimes(EventResponse response){
        try{
            response.getEventProperties().getTimes().clear();
            dao.update(response);
            return true;
        }
        catch(Exception e){
            LOGGER.error("Failed to clear times: {} ", e.getMessage());
            return false;
        }
    }

}
