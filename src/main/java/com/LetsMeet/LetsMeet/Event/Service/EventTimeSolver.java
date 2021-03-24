package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.GradedProperty;


public class EventTimeSolver {


    
    private List<GradedProperty<DateTimeRange>> solution;
    private List<EventResponse> responses;

    public EventTimeSolver(Event event, List<EventResponse> responses){
        this.responses = responses;
        this.solution = new ArrayList<>();

        // Prepare base solution. Take the event times and set optimality to 0 (no users can attend)
        for (DateTimeRange o : event.getEventProperties().getTimes()){
            solution.add(new GradedProperty<DateTimeRange>(o, 0));
        }
    }

    public List<GradedProperty<DateTimeRange>> getSolution(){
        return this.solution;
    }

    public List<GradedProperty<DateTimeRange>> solve(long minDurationMins){
        Duration minDuration = Duration.ofMinutes(minDurationMins);
        //TODO sort solution list first from early to late

        // move through each response and find intersection of times
        for (EventResponse response : this.responses){
            
            for (DateTimeRange range : response.getEventProperties().getTimes()){

                int start = splitRange(range.getStart(),true);   // Attempt split at start
                int end = splitRange(range.getEnd(),false);   // Attempt split at end
                
                for (int i = start; i < end; i++){          // Increment optimality of each OptimalityRange until end of range
                    if (solution.get(i).getProperty().getDuration().compareTo(minDuration) < 0) continue; //TODO remove element
                    solution.get(i).grade++;
                }
            }
        }
        return this.solution;
    }

    // Splits a DateTimeRange at time point and returns index of new range. New range is created in front of time point
    private int splitRange(ZonedDateTime time, boolean forward){
        int rangeIndex = getDateTime(time);

        if (rangeIndex == -1)
            rangeIndex = (forward) ? getFirst(time) : getLast(time);

        solution.add(rangeIndex + 1, new GradedProperty<>(new DateTimeRange(time, solution.get(rangeIndex).getProperty().getEnd()), solution.get(rangeIndex).grade));     // New range
        solution.get(rangeIndex).getProperty().setEnd(time);                                                                                                               // Shrink old range
        return rangeIndex + 1;
    }

    // Returns OptimalityRange of the given time (if it exists)
    private int getDateTime(ZonedDateTime time){
        for (int i = 0; i<solution.size(); i++){
            if (!solution.get(i).getProperty().getStart().isAfter(time) && !solution.get(i).getProperty().getEnd().isBefore(time)) return i;
        }
        return -1;
    }

    private int getFirst(ZonedDateTime time){
        for (int i = 0; i<solution.size(); i++){
            if (!solution.get(i).getProperty().getStart().isBefore(time)) return i;
        }
        return -1;
    }

    private int getLast(ZonedDateTime time){
        for (int i = solution.size() - 1; i > 0; i--){
            if (!solution.get(i).getProperty().getStart().isAfter(time)) return i;
        }
        return -1;
    }

    // return only those ranges which have the duration given or greater
    public List<GradedProperty<DateTimeRange>> withDuration(Duration duration){
        List<GradedProperty<DateTimeRange>> withDuration = new ArrayList<>();

        for (var or : this.solution)
            if (or.getProperty().getDuration().compareTo(duration) >= 0) withDuration.add(or);
        
        this.solution = withDuration;
        return withDuration;
    }

    // return only those ranges which fit within the responses
    public List<GradedProperty<DateTimeRange>> withResponses(List<EventResponse> responses){
        List<GradedProperty<DateTimeRange>> withResponses = new ArrayList<>();

        // This pains me
        for (EventResponse er : responses){
            for (DateTimeRange dtr : er.getEventProperties().getTimes()){
                for (var or : this.solution){
                    if (!dtr.getEnd().isAfter(or.getProperty().getEnd()) && !dtr.getStart().isBefore(or.getProperty().getStart())){
                        withResponses.add(or);
                    }
                }
            }
        }
        this.solution = withResponses;
        return withResponses;
    }

}
