package com.LetsMeet.LetsMeet.Event.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.Event.Model.Event;

public class EventService implements EventServiceInterface {

    @Override
    public void createEvent(Event event) {
        // TODO Auto-generated method stub

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
    public void addUser(String eventUuid, String userUuid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUser(String eventUuid, String userUuid) {
        // TODO Auto-generated method stub

    }
    
}
