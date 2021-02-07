package com.LetsMeet.LetsMeet.Event.Service;

import java.util.Collection;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService implements EventServiceInterface {

    @Autowired
    EventDao eventDao;

    @Autowired
    EventPermissionDao permissionDao;

    @Override
    public void createEvent(Event event) {
        eventDao.save(event);

    }

    @Override
    public void updateEvent(String uuid, Event event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteEvent(String uuid) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<Event> getEvents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Event> getUserEvents(String uuid) {
        // TODO Auto-generated method stub
        return null;
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
 
}
