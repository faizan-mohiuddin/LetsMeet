//-----------------------------------------------------------------
// EventService.java
// Let's Meet 2021
//
// Implements the operations possible on an event.

package com.LetsMeet.LetsMeet.Event.Service;

//-----------------------------------------------------------------

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.User.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventResponseDao;
import com.LetsMeet.LetsMeet.Event.Model.Constraint;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Event.Model.Variable;
import com.LetsMeet.LetsMeet.Event.Model.Variables.Location;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//-----------------------------------------------------------------

@Service
public class EventService implements EventServiceInterface {

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventService.class);

    // Components
    @Autowired
    EventDao eventDao;

    @Autowired
    EventPermissionDao permissionDao;

    @Autowired
    ConditionSetService conditionSetService;

    @Autowired
    UserService userService;


    /* -- CRUD operations -- */

    // Create a new event:
    // Creates a new event and sets the given user (typically the one creating the event) administrative rights over it. 

    @Override
    public String createEvent(String name, String desc, String location, String UserUUID) {
        // Generate EventUUID
        UUID eventUUID = generateEventUUID(name, desc, location);

        // Create an Event POJO
        Event event = new Event(eventUUID.toString(), name, desc, location,conditionSetService.createDefault().getUUID());

        // Ensure event is saved to persistent storage
        if (eventDao.save(event)) {
            
            // Set the User as owner (admin) of Event
            EventPermission record = new EventPermission(eventUUID.toString(), UserUUID, true);

            if (permissionDao.save(record)) {
                return "Event successfully created.";
            } else {
                return "Error creating event.";
            }
        } else {
            return "Error creating event.";
        }
    }


    // Internal method to create a special UUID for an event. Allows the UUID to be deconstructed later
    private static UUID generateEventUUID(String name, String desc, String location) {
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + desc + location + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

    // Update an existing event
    // Checks if given user has edit privileges. If so sends Event object to DAO
    @Override
    public String updateEvent(User user, Event event, String name, String desc, String location) {
        // Check user has permission
        if(this.checkOwner(event.getUUID(), user.getUUID())) {

            // Switch values
            event.switchName(name);
            event.switchDesc(desc);
            event.switchLocation(location);

            // Update in DB
            if (eventDao.update(event)) {
                return "Event successfully updated";
            }
            return "Error updating Event";
        }
        return "You do not have permission to update this Event";
    }


    // Delete an existing event
    // Checks if given user has edit privileges first. Also deletes Events ConditionSet.
    @Override
    public String deleteEvent(String uuid, User user) {
        // Check if user has permission to delete event
        Optional<EventPermission> record = permissionDao.get(UUID.fromString(uuid), user.getUUID());
        if (record.isPresent()) {
            if (!record.get().getIsOwner()) {
                return "You do not have permission to delete this event";
            }
        } else {
            return "You do not have permission to delete this event";
        }

        Event event = eventDao.get(uuid).get();
        conditionSetService.delete(event.getConditions());
        if (eventDao.delete(UUID.fromString(uuid))) {
            return "Event successfully deleted.";
        } else {
            return "Error deleting event";
        }
    }

    // Returns all Events on the system (expensive)
    @Override
    public Collection<Event> getEvents() {
        return eventDao.getAll().get();
    }

    // Returns a single event as specified
    public Event getEvent(String UUID) {
        Optional<Event> event = eventDao.get(UUID);
        if(event.isPresent()){
            return event.get();
        }
        return null;
    }

    public void setProperty(Event event, String key, String value){
        event.getProperties().set(key, value);
        eventDao.update(event);
    }

    public String getProperty(Event event, String key){
        return event.getProperties().get(key);
    }

    //-----------------------------------------------------------------
    /* -- User related operations --
    
        Users are linked to an event either through an EventPermission, which records whether they own an event and can edit it
        or through a EventResponse which records their "response" to an event and is managed in a separate ResponseManager.java
    */

    // Returns all events which given user is owner of
    @Override
    public Collection<Event> getUserEvents(String userUUID) {
        List<Event> events = new ArrayList<>();

        // Get list of users permissions
        List<EventPermission> eventPerms = permissionDao.get(userUUID).get();

        // Get each event on permissions list
        for (EventPermission e : eventPerms){
            events.add(eventDao.get(e.getEvent()).get());
        }
        return events;
    }

    // Set the boolean isOwner for a user/event pair
    @Override
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

    // Returns list of users in an event
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
    @Override
    public boolean setTimeRange(UUID eventUuid, List<DateTimeRange> ranges) {
        try{
            Event event = eventDao.get(eventUuid).get();
            conditionSetService.addTimeRanges(conditionSetService.get(event.getConditions()), ranges);
            return true;
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<DateTimeRange> getTimeRange(UUID eventUUID) {
        Event event = eventDao.get(eventUUID).get();
        return conditionSetService.getTimeRange(conditionSetService.get(event.getConditions())).get();
    }

    @Override
    public boolean setServices(UUID eventUuid, List<String> services) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getServices(UUID event) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean setLocations(UUID eventUuid, List<Location> locations) {
        try{
            Event event = eventDao.get(eventUuid).get();
            conditionSetService.addLocation(conditionSetService.get(event.getConditions()), locations);
            return true;
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    public List<Location> getLocation(UUID eventUUID) {
        Event event = eventDao.get(eventUUID).get();
        return conditionSetService.getLocation(conditionSetService.get(event.getConditions())).get();
    }



    // Depreciated methods. These are no longer required and should be removed when possible
    // ---------------------------------------------------------------------------------------------------------------

    @Deprecated // Use SetPermission to "join" user as Owner 
    public String joinEvent(String EventUUID, String UserUUID) {
        // Check event exists
        Event data = eventDao.get(EventUUID).get();

        if (data == null) {
            return "Event Doesnt exist";
        }

        // Check that user is not already in event
        Optional<EventPermission> checker = permissionDao.get(UUID.fromString(EventUUID), UUID.fromString(UserUUID));

        if (!checker.isPresent()) {
            // Add user to event not as an owner
            boolean result = permissionDao.save(new EventPermission(EventUUID, UserUUID, false));

            if (!result) {
                return "Error adding user to event";
            } else {
                return "User added to event";
            }
        } else {
            return "You are already a participant of this event.";
        }
    }


   /* ----- DEPRECIATED ------
   Be aware that these methods shall be removed in future versions and should not be referenced
   ---------------------------*/


    @Deprecated // Use SetPermission to "join" user as Owner 
    public String leaveEvent(String EventUUID, String UserUUID) {
        // Check that user is in event
        Optional<EventPermission> checker = permissionDao.get(UUID.fromString(EventUUID), UUID.fromString(UserUUID));
        if (!checker.isPresent()) {
            return "You have not joined this event";
        }

        if (permissionDao.delete(EventUUID, UserUUID)) {
            return "Successfully left event.";
        } else {
            return "Error leaving event";
        }
    }

    
    @Deprecated // Use specific methods for adding variables (e.g timerange)
    public boolean addVariable(UUID eventUUID, Variable<?> variable) {
        if (eventDao.get(eventUUID).isPresent()) {
            Event event = eventDao.get(eventUUID).get();
            //event.getConditions().addVariable(variable);
            if (eventDao.update(event).booleanValue()) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Deprecated // Use specific methods for adding constraints
    public boolean addConstraint(UUID eventUUID, Constraint<?> constraint) {
        if (eventDao.get(eventUUID).isPresent()) {
            Event event = eventDao.get(eventUUID).get();
            //event.getConditions().addConstraint(constraint);
            if (eventDao.update(event).booleanValue()) {
                return true;
            }
            return false;
        }
        return false;
    }
}


