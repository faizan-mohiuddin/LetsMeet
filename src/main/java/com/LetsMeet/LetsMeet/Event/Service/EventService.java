//-----------------------------------------------------------------
// EventService.java
// Let's Meet 2021
//
// Implements the operations possible on an event.

package com.LetsMeet.LetsMeet.Event.Service;

import java.io.IOException;
import java.time.Duration;

//-----------------------------------------------------------------

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventResultDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.EventResult;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//-----------------------------------------------------------------

@Service
public class EventService{

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventService.class);

    // Components
    @Autowired
    EventDao eventDao;

    @Autowired
    EventPermissionDao permissionDao;

    @Autowired
    UserService userService;

    @Autowired
    EventResponseService responseService;

    @Autowired
    EventResultDao resultDao;


    /* -- CRUD operations -- */

    // Create a new event:
    // Creates a new event and sets the given user (typically the one creating the event) administrative rights over it. 

    public Event createEvent(String name, String desc, String location, String UserUUID) throws IOException{

        try{
            // Prepare and save new event object with specialized UUID
            UUID eventUUID = generateEventUUID(name, desc, location);
            Event event = new Event(eventUUID, name, desc, location, EventProperties.getEmpty());
            eventDao.save(event);

            // Prepare and save new EventPermission to record user as an editor of the event
            EventPermission record = new EventPermission(eventUUID.toString(), UserUUID, true);
            permissionDao.save(record);

            return event;
        }
        catch(Exception e){
            throw new IOException("Unable to create new event");
        }
    }


    // Internal method to create a special UUID for an event. Allows the UUID to be deconstructed later
    private static UUID generateEventUUID(String name, String desc, String location) {
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + desc + location + strTime;
        return UUID.nameUUIDFromBytes(uuidData.getBytes());
    }

    // Update an existing event
    // Checks if given user has edit privileges. If so sends Event object to DAO
    public Boolean updateEvent(User user, Event event) throws IllegalArgumentException, IOException{

        // Check user has edit permissions
        if(!this.checkOwner(event.getUUID(), user.getUUID())) {
            throw new IllegalArgumentException("Provided User does not have sufficient privileges. User= <" + user.getUUID() + ">");
        }
        // Update event in persistance layer
        return eventDao.update(event);

    }


    // Delete an existing event
    // Checks if given user has edit privileges first. Also deletes Events ConditionSet.
    public Boolean deleteEvent(Event event, User user) throws IllegalArgumentException{

        try{
            if(!this.checkOwner(event.getUUID(), user.getUUID())) {
                throw new IllegalArgumentException("Provided User does not have sufficient privileges. User= <" + user.getUUID() + ">");
            }

            // Perform any pre-delete cleanup here
            //TODO delete event images
            //TODO delete event responses
            
            // Delete Event
            return eventDao.delete(event);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Unable to delete Event <" + event.getUUID() + ">" + e.getMessage());
        }
    }



    // Returns all Events on the system (expensive)
    public Collection<Event> getEvents() {
        try{
            return eventDao.getAll().get();
        }
        catch(Exception e){
            LOGGER.error("Could not get all events: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // Returns general search results
    public List<Event> search(String term){
        String likeTerm = "'%" + String.format("%s", term) + "%'";
        String query = String.format("SELECT * FROM Event WHERE Event.EventUUID = '%s' OR " +
                        "Event.Name LIKE %s OR Event.Description LIKE %s OR Event.Location LIKE %s",
                term, likeTerm, likeTerm, likeTerm);
        Optional<List<Event>> events = eventDao.search(query);
        if(events.isPresent()){
            return events.get();
        }
        return null;
    }

    // Returns a single event as specified
    public Event getEvent(String eventUUID) throws IllegalArgumentException {

        try{

            Optional<Event> event = eventDao.get(eventUUID);        

            if(event.isPresent()){return event.get();}
            else throw new IllegalArgumentException("No Event found for UUID <" + eventUUID + ">");

        }
        catch (IOException e){
            throw new IllegalArgumentException("No Event found for UUID <" + eventUUID + ">");
        }
    }

    public void setProperty(Event event, String key, String value) throws IOException{
        event.getProperties().set(key, value);
        eventDao.update(event);
    }

    public String getProperty(Event event, String key){
        return event.getProperties().get(key);
    }

    public EventResult calculateResults(Event event, User user, int duration, boolean requiredUsers) throws IllegalArgumentException{
        try{
            if (permissionDao.get(event.getUUID(), user.getUUID()).orElseThrow().getIsOwner() != true) throw new IllegalArgumentException("Insufficient privileges");

            EventResult result;
            List<EventResponse> responses = responseService.getResponses(event);
            result = resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));

            List<EventResponse> requiredResponses = new ArrayList<>();
            for (EventResponse response : responses){
                if (response.getRequired()) requiredResponses.add(response);
            }
        
            EventTimeSolver timeSolver = new EventTimeSolver(getEvent(event.getUUID().toString()), responses);

            timeSolver.solve(1);

            if(duration > 4) timeSolver.withDuration(Duration.ofMinutes(duration));
            if(requiredUsers) timeSolver.withResponses(requiredResponses);

            result.setUniqueResponses(responses.size());
            result.setDateTimeRanges(timeSolver.getSolution());
            resultDao.update(result);
            return result;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate results: " + e.getMessage());
        }
    }

    private EventResult newEventResult(Event event){
        try{
        EventResult result = new EventResult(event.getUUID(), null);
        resultDao.save(result);
        return result;
        }
        catch (Exception e){
            return null;
        }
    }

    //-----------------------------------------------------------------
    /* -- User related operations --
    
        Users are linked to an event either through an EventPermission, which records whether they own an event and can edit it
        or through a EventResponse which records their "response" to an event and is managed in a separate ResponseManager.java
    */

    // Returns all events which given user is owner of
    public Collection<Event> getUserEvents(User user) {
        List<Event> events = new ArrayList<>();

        try{
            // Get list of users permissions
            List<EventPermission> eventPerms = permissionDao.get(user.getUUID().toString()).get();

            // Get each event on permissions list
            for (EventPermission e : eventPerms){
                events.add(eventDao.get(e.getEvent()).get());
            }
            return events;
        }
        catch (Exception e){
            throw new IllegalArgumentException("No Event found for UUID <" + user.getUUID() + ">");
        }
    }

    // Set the boolean isOwner for a user/event pair
    public Boolean setPermissions(Event event, User user, Boolean owner) {
        try{
            permissionDao.save(new EventPermission(event.getUUID(), user.getUUID(), owner));
            return true;
        }
        catch (Exception e){
            LOGGER.error("Could not set event permissions {}", e.getMessage());
            return false;
        }
    }

    // Returns true if given User owns event, otherwise false.
    public boolean checkOwner(UUID eventUUID, UUID userUUID) {
        Optional<EventPermission> response = permissionDao.get(eventUUID, userUUID);

        if (response.isPresent()) {
            return response.get().getIsOwner();
        }
        return false;
    }

    // Returns list of users who have responded to an event
    public List<User> EventsUsers(UUID eventUUID){
        List<User> users = new ArrayList<>();

        // Get list of users permissions
        List<EventPermission> eventPerms = permissionDao.get(eventUUID.toString()).get();

        // Get each user on permissions list
        for(EventPermission e : eventPerms){
            users.add(userService.getUserByUUID(e.getUser().toString()));
        }
        return users;
    }

    //-----------------------------------------------------------------
    /* -- ConditionSet related operations --
    
        Each Event has it's own ConditionSet. This is used to record any data which logically belongs to an event
        such as the range of dates it could take place. Dates that a user could make it to an event belong to that user
        not the event and as such should be stored within their Response and not, NOT, in the events ConditionSet. 
        Constraints that a User must be at the event are stored here. 

        This section is mostly a gateway to the separate ConditionSet manager, which handles the heavy logic
        Here, get the Event, then call the ConditionSetService with the UUID of the ConditionSet attached to the event.
    */


    // Will add a new period to the time range constraint - use to set the periods that the event could take place.
    public boolean setTimeRange(UUID eventUuid, List<DateTimeRange> times) {
        try{
            Event event = eventDao.get(eventUuid).get();
            event.getEventProperties().setTimes(times);
            return eventDao.update(event);
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    public List<DateTimeRange> getTimeRange(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getTimes();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public boolean setFacilities(UUID eventUUID, List<String> facilities) {
        try{
            Event event = eventDao.get(eventUUID).get();
            event.getEventProperties().setFacilities(facilities);
            return true;
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }
    
    public List<String> getFacilities(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getFacilities();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public boolean setLocations(UUID eventUuid, Location location) {
        try{
            Event event = eventDao.get(eventUuid).get();
            event.getEventProperties().setLocation(location);
            return true;
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    public Location getLocation(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getLocation();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }
}


