package com.LetsMeet.LetsMeet.Event.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.Event.Model.Event;

public interface EventServiceInterface {
    public abstract void createEvent(Event event);
    public abstract void updateEvent(String uuid, Event event);
    public abstract void deleteEvent(String uuid);
    public abstract Collection<Event> getEvents();

    public abstract Collection<Event> getUserEvents(String uuid);
    public abstract void setPermissions(String eventUuid, String userUuid, Boolean owner);
    public abstract void addResponse(String eventUuid, String userUuid, String conditonSetUUID);
    public abstract void removeResponse(String eventUuid, String userUuid);
    
}
