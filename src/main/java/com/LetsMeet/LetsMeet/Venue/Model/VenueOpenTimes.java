package com.LetsMeet.LetsMeet.Venue.Model;

import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Venue.Controller.VenueControllerWeb;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER= LoggerFactory.getLogger(VenueOpenTimes.class);

    public VenueOpenTimes(UUID venueUUID){
        this.VenueUUID = venueUUID;
    }

    public VenueOpenTimes(){

    }

    public void setTime(String day, String open, String close){
        // Get rid of double quotes
        day = day.replaceAll("\"", "");
        open = open.replaceAll("\"", "");
        close = close.replaceAll("\"", "");

        try {
            // Make sure to add to times array in order of day
            int d = Integer.parseInt(day);
            List<String> l = new ArrayList<>();
            l.add(day);
            l.add(open);
            l.add(close);
            if (d == 1 || this.times.size() == 0) {
                this.times.add(l);
            } else {
                int index = 0;
                Boolean added = false;

                for (List<String> t : this.times) {
                    int tday = Integer.parseInt(t.get(0));
                    if (d < tday) {
                        this.times.add(index, l);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    this.times.add(l);
                }

            }
        }catch(Exception e){
            // If code reaches here then, day or time is not in correct format
            LOGGER.warn("Error setting venue time range: {}", e.getMessage());
        }
    }

    public List<List<String>> getTimes(){
        return this.times;
    }

    public UUID getUUID(){
        return this.VenueUUID;
    }

    public void setVenueUUID(UUID venueUUID){
        this.VenueUUID = venueUUID;
    }

    public void setTimes(String JSON){
        // Process JSON
        Gson gson = new Gson();
        JsonArray obj = gson.fromJson(JSON, JsonArray.class);

        for(JsonElement o : obj){
            JsonObject ob = o.getAsJsonObject();
            this.setTime(ob.get("day").toString(), ob.get("open").toString(), ob.get("close").toString());
        }
        // Call setTime
    }

    public int numTimes(){
        return this.times.size();
    }

    public List<List<String>> getTimesWithDays(){
        List<List<String>> timesDays = new ArrayList<>();
        for(List<String> t : this.times){
            Collections.replaceAll(t, "1", "Sunday");
            Collections.replaceAll(t, "2", "Monday");
            Collections.replaceAll(t, "3", "Tuesday");
            Collections.replaceAll(t, "4", "Wednesday");
            Collections.replaceAll(t, "5", "Thursday");
            Collections.replaceAll(t, "6", "Friday");
            Collections.replaceAll(t, "7", "Saturday");

            timesDays.add(t);
        }
        return timesDays;
    }

    public List<List<String>> getTimesWithIndex(){
        List<List<String>> timesIndex = new ArrayList<>();
        int index = 0;
        for(List<String> t : this.times){
            t.add(String.valueOf(index));
            index += 1;
            timesIndex.add(t);
        }
        return timesIndex;
    }
}
