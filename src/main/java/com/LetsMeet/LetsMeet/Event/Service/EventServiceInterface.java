package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.User.Model.User;

public interface EventServiceInterface {
    public abstract String createEvent(String name, String desc, String location, String UserUUID);
    public abstract void updateEvent(String uuid, Event event);
    public abstract String deleteEvent(String uuid, User user);
    public abstract Collection<Event> getEvents();

    public abstract Collection<Event> getUserEvents(String uuid);
    public abstract void setPermissions(String eventUuid, String userUuid, Boolean owner);

    // Management of ConditionSets

    // Time
    public abstract void setTimeRange(UUID event, List<DateTimeRange> ranges);
    public abstract List<DateTimeRange> getTimeRange(UUID event);

    // Services
    public abstract void setServices(UUID event, List<String> services);
    public abstract List<String> getServices(UUID event);
    
}
