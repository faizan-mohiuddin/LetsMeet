package com.LetsMeet.LetsMeet.Event.Model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;

public class EventResult implements Serializable{
    
    private static final long serialVersionUID = -248989502245064846L;

    public static class OptimalityRange implements Serializable{
        private static final long serialVersionUID = -222100436555521092L;
        public DateTimeRange range;
        public Integer optimality;

        public OptimalityRange(DateTimeRange range, Integer optimality){this.range = range; this.optimality = optimality;}
    }

    private UUID eventUUID;
    private List<OptimalityRange> dateTimeRanges;
    private int uniqueResponses;
    
    public EventResult(UUID eventUUID, List<OptimalityRange> dateTimeRanges){
        this.eventUUID = eventUUID;
        this.dateTimeRanges = dateTimeRanges;
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
}
