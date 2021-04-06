package com.LetsMeet.LetsMeet.Venue.Model;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
* 1 = Sunday
* 2 = Monday
* 3 = Tuesday
* 4 = Wednesday
* 5 = Thursday
* 6 = Friday
* 7 = Saturday
*/

public class VenueOpenTimes {
    UUID VenueUUID;
    List<List<String>> times = new ArrayList<>(); // [[day, open, close]] ([[int, str, str]])

    public VenueOpenTimes(){

    }

    public void setTime(String day, String open, String close){
        // Make sure to add to times array in order of day
        int d = Integer.parseInt(day);
        List<String> l = new ArrayList<>();
        l.add(day);
        l.add(open);
        l.add(close);
        if(d == 1 || this.times.size() == 0){
            this.times.add(l);
        }else{
            int index = 0;
            for(List<String> t : this.times){
                int tday = Integer.parseInt(t.get(0));
                if(d < tday){
                    this.times.add(index, l);
                }
            }
        }
    }

    public List<List<String>> getTimes(){
        return this.times;
    }

    public UUID getUUID(){
        return this.VenueUUID;
    }
}
