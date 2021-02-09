package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;

import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService implements EventServiceInterface {

    @Autowired
    EventDao eventDao;

    @Autowired
    EventPermissionDao permissionDao;

    @Override
    public String createEvent(String name, String desc, String location, String UserUUID) {
        // Generate EventUUID
        UUID eventUUID = generateEventUUID(name, desc, location);
        Event event = new Event(eventUUID.toString(), name, desc, location);
        if(eventDao.save(event)){
            // Add record to hasUsers
            EventPermission record = new EventPermission(eventUUID.toString(), UserUUID, true);

            if(permissionDao.save(record)) {
                return "Event successfully created.";
            }else{
                return "Error creating event.";
            }
        }else{
            return "Error creating event.";
        }
    }

    @Override
    public void updateEvent(String uuid, Event event) {
        // TODO Auto-generated method stub

    }

    @Override
    public String deleteEvent(String uuid) {
        // TODO Auto-generated method stub
        if(eventDao.delete(UUID.fromString(uuid))){
            return "Event successfully deleted.";
        }else{
            return "Error deleting event";
        }
    }

    @Override
    public Collection<Event> getEvents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Event> getUserEvents(String uuid) {
        // TODO Auto-generated method stub
        return eventDao.getUserEvents(uuid).get();
    }

    @Override
    public void setPermissions(String eventUuid, String userUuid, Boolean owner) {
        permissionDao.save(new EventPermission(UUID.fromString(eventUuid), UUID.fromString(userUuid), owner));
    }

    @Override
    public void addResponse(String eventUuid, String userUuid, String conditonSetUUID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeResponse(String eventUuid, String userUuid) {
        // TODO Auto-generated method stub

    }

    // Other methods
    //---------------------------------------------------------------------------------------------------------------
    public Event getEvent(String UUID){
        return eventDao.get(UUID).get();
    }

    public String joinEvent(String EventUUID, String UserUUID){
        // Check event exists
        Event data = eventDao.get(EventUUID).get();

        if(data == null){
            return "Event Doesnt exist";
        }

        // Add user to event not as an owner
        boolean result = permissionDao.save(new EventPermission(EventUUID, UserUUID,false));

        if(!result){
            return "Error adding user to event";
        }else{
            return "User added to event";
        }
    }

    public String leaveEvent(String EventUUID, String UserUUID){
        if(permissionDao.delete(EventUUID, UserUUID)){
            return "Successfuly left event";
        }else{
            return "Error leaving event";
        }
    }

    // private methods
    private static UUID generateEventUUID(String name, String desc, String location){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + desc + location + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }
}
