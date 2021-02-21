//-----------------------------------------------------------------
// EventResponseService.java
// Let's Meet 2021
//
// Implements the operations possible on an EventResponse.

package com.LetsMeet.LetsMeet.Event.Service;

//-----------------------------------------------------------------
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import com.LetsMeet.LetsMeet.Event.DAO.EventResponseDao;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
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

    // Service for manipulating the responses condition set
    @Autowired
    ConditionSetService conditionSetService;

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventService.class);

    // Creation
    //-----------------------------------------------------------------
    public EventResponse createResponse(User user, Event event){
        EventResponse response = new EventResponse(event.getUUID(), user.getUUID(), conditionSetService.createDefault().getUUID());
        dao.save(response);
        return response;
    }

    public EventResponse getResponse(User user, Event event){
        return dao.get(user.getUUID(), event.getUUID()).orElseThrow(IllegalArgumentException::new);
    }

    // Modification
    //-----------------------------------------------------------------

    // set times
    public boolean setTimes(User user, Event event, List<DateTimeRange> ranges){
        try{
        EventResponse response = dao.get(user.getUUID(), event.getUUID()).orElse(createResponse(user, event));
        conditionSetService.addTimeRanges(response.getConditionSet(), ranges );
        return true;
        }
        catch (Exception e){
            LOGGER.error("Failed to add response: {} ", e.getMessage());
            return false;
        }
    }

    // get times
    public Optional<List<DateTimeRange>> getTimes(User user, Event event){
        try{
            // Call getTimeRange on ConditionSet which is member of the response between this user and this event
            return Optional.of(conditionSetService.getTimeRange(dao.get(event.getUUID(),user.getUUID()).get().getConditionSet()).get());
        }
        catch (Exception e){
            LOGGER.error("Failed to get times: {} ", e.getMessage());
            return Optional.empty();
        }
    }

}
