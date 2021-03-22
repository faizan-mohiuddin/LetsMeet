package com.LetsMeet.LetsMeet.Event.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;

public class EventResult implements Serializable{
    
    private static final long serialVersionUID = -248989502245064846L;

    public static class OptimalityRange implements Serializable{
        private static final long serialVersionUID = -222100436555521092L;
        public DateTimeRange range;
        public Integer optimality;

        public OptimalityRange(DateTimeRange range, Integer optimality){this.range = range; this.optimality = optimality;}
    }

    public static class OptimalityLocation implements Serializable{
        private static final long serialVersionUID = -222100466555521092L;
        public Location location;
        public Integer optimality;

        public OptimalityLocation(Location range, Integer optimality){this.location = range; this.optimality = optimality;}
    }


    private UUID eventUUID;
    private List<OptimalityRange> dateTimeRanges;
    private List<OptimalityLocation> locations;
    private int uniqueResponses;
    
    public EventResult(UUID eventUUID, List<OptimalityRange> dateTimeRanges){
        this.eventUUID = eventUUID;
        this.dateTimeRanges = dateTimeRanges;
        this.locations = new ArrayList<>();
        uniqueResponses = 0;
    }

    public List<OptimalityRange> getDateTimeRanges() {
        return dateTimeRanges;
    }

    public void setDateTimeRanges(List<OptimalityRange> dateTimeRanges) {
        this.dateTimeRanges = dateTimeRanges;
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

    public List<OptimalityLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<OptimalityLocation> locations) {
        this.locations = locations;
    }
}
