package com.LetsMeet.LetsMeet.Event.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;

public class EventProperties implements Serializable {

    private static final long serialVersionUID = 3789664317595140062L;
    
    private List<DateTimeRange> times;
    private List<String> facilities;
    private Location location;

    public static EventProperties getEmpty(){
        return new EventProperties(new ArrayList<>(), new ArrayList<>(), new Location("null", 0.0, 0.0, 0.0));
    }
    
    public EventProperties(List<DateTimeRange> times, List<String> facilities, Location location) {
        this.times = times;
        this.facilities = facilities;
        this.location = location;
    }

    public List<DateTimeRange> getTimes() {
        return times;
    }

    public void setTimes(List<DateTimeRange> times) {
        this.times = times;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
}
