package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Period;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.EventResult.OptimalityRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;


public class EventTimeSolver {


    
    private ArrayList<OptimalityRange> solution;
    private List<EventResponse> responses;

    public EventTimeSolver(Event event, List<EventResponse> responses){
        this.responses = responses;
        this.solution = new ArrayList<>();

        // Prepare base solution. Take the event times and set optimality to 0 (no users can attend)
        for (DateTimeRange o : event.getEventProperties().getTimes()){
            solution.add(new OptimalityRange(o,0));
        }
    }

    public List<OptimalityRange> solve(long minDurationMins){
        Duration minDuration = Duration.ofMinutes(minDurationMins);
        //TODO sort solution list first from early to late

        // move through each response and find intersection of times
        for (EventResponse response : this.responses){
            
            for (DateTimeRange range : response.getEventProperties().getTimes()){

                int start = splitRange(range.getStart(),true);   // Attempt split at start
                int end = splitRange(range.getEnd(),false);   // Attempt split at end
                
                for (int i = start; i < end; i++){          // Increment optimality of each OptimalityRange until end of range
                    if (solution.get(i).range.getDuration().compareTo(minDuration) < 0) continue; //TODO remove element
                    solution.get(i).optimality++;
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

        solution.add(rangeIndex + 1, new OptimalityRange(new DateTimeRange(time, solution.get(rangeIndex).range.getEnd()), solution.get(rangeIndex).optimality));     // New range
        solution.get(rangeIndex).range.setEnd(time);                                                                                                                    // Shrink old range
        return rangeIndex + 1;
    }

    // Returns OptimalityRange of the given time (if it exists)
    private int getDateTime(ZonedDateTime time){
        for (int i = 0; i<solution.size(); i++){
            if (!solution.get(i).range.getStart().isAfter(time) && !solution.get(i).range.getEnd().isBefore(time)) return i;
        }
        return -1;
    }

    private int getFirst(ZonedDateTime time){
        for (int i = 0; i<solution.size(); i++){
            if (!solution.get(i).range.getStart().isBefore(time)) return i;
        }
        return -1;
    }

    private int getLast(ZonedDateTime time){
        for (int i = solution.size() - 1; i > 0; i--){
            if (!solution.get(i).range.getStart().isAfter(time)) return i;
        }
        return -1;
    }

    // return only those ranges which have the duration given or greater
    public static List<OptimalityRange> withDuration(List<OptimalityRange> ranges, Duration duration){
        List<OptimalityRange> withDuration = new ArrayList<>();

        for (OptimalityRange or : ranges)
            if (or.range.getDuration().compareTo(duration) >= 0) withDuration.add(or);
        
        return withDuration;
    }

    // return only those ranges which fit within the responses
    public static List<OptimalityRange> withResponses(List<OptimalityRange> ranges, List<EventResponse> responses){
        List<OptimalityRange> withResponses = new ArrayList<>();

        // This pains me
        for (EventResponse er : responses){
            for (DateTimeRange dtr : er.getEventProperties().getTimes()){
                for (OptimalityRange or : ranges){
                    if (!dtr.getEnd().isAfter(or.range.getEnd()) && !dtr.getStart().isBefore(or.range.getStart())){
                        withResponses.add(or);
                    }
                }
            }
        }
        return withResponses;
    }

}
