package com.LetsMeet.LetsMeet.Event.Model;

import java.io.Serializable;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Model.Properties.ResultProperty;

public class EventResult implements Serializable{
    
    private static final long serialVersionUID = -248989502245064846L;



    private ResultProperty<DateTimeRange> dates;
    private ResultProperty<Location> locations;
    private UUID eventUUID;
    private int uniqueResponses;



    public EventResult(UUID eventUUID, ResultProperty<DateTimeRange> dates, ResultProperty<Location> locations, int uniqueResponses) {
    this.dates = dates;
    this.locations = locations;
    this.eventUUID = eventUUID;
    this.uniqueResponses = uniqueResponses;
    }

    public EventResult(UUID eventUUID, ResultProperty<DateTimeRange> dates, ResultProperty<Location> locations) {
        this(eventUUID, dates, locations, 0);
    }

    public EventResult(UUID eventUUID) {
        this(eventUUID, new ResultProperty<>() , new ResultProperty<>());
    }


    public UUID getEventUUID() {
        return eventUUID;
    }

    public void setEventUUID(UUID eventUUID) {
        this.eventUUID = eventUUID;
    }

    public int getUniqueResponses() {
        return uniqueResponses;
    }

    public void setUniqueResponses(int uniqueResponses) {
        this.uniqueResponses = uniqueResponses;
    }

    public float getPercent(int optimality){
        return ((float) optimality /(float)this.uniqueResponses);
    }


    public ResultProperty<DateTimeRange> getDates() {
        return dates;
    }


    public void setDates(ResultProperty<DateTimeRange> dates) {
        this.dates = dates;
    }


    public ResultProperty<Location> getLocations() {
        return locations;
    }


    public void setLocations(ResultProperty<Location> locations) {
        this.locations = locations;
    }
}
