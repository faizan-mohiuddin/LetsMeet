//-----------------------------------------------------------------
// EventResponseService.java
// Let's Meet 2021
//
// Implements the operations possible on an EventResponse.

package com.LetsMeet.LetsMeet.Event.Service;

import org.apache.catalina.connector.Response;
//-----------------------------------------------------------------
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Boolean deleteResponse(User user, Event event) throws IllegalArgumentException{
        try{
            EventResponse response = dao.get(event.getUUID(), user.getUUID()).orElseThrow();
            
            if (response.getRequired() == true){
                LOGGER.debug("Can't delete response between User <{}> and Event <{}> : It is marked as required. Attempting to clear contents instead.",user.getUUID(), event.getUUID());
                return this.clearResponse(user, event);
            }

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
