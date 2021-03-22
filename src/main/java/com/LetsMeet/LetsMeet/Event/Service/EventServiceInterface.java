package com.LetsMeet.LetsMeet.Event.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.User.Model.User;

public interface EventServiceInterface {
    public abstract Event createEvent(String name, String desc, String location, String UserUUID) throws IOException;
    public abstract String updateEvent(User user, Event event, String name, String desc, String location);
    public abstract String deleteEvent(String uuid, User user);
    public abstract Collection<Event> getEvents();

    public abstract Collection<Event> getUserEvents(String uuid);
    public abstract Boolean setPermissions(Event event, User user, Boolean owner);

    // Management of ConditionSets

    // Time
    public abstract boolean setTimeRange(UUID event, List<DateTimeRange> ranges);
    public abstract List<DateTimeRange> getTimeRange(UUID event);

    // Services
    public abstract boolean setServices(UUID event, List<String> services);
    public abstract List<String> getServices(UUID event);
    
}
