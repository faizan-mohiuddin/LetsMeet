//-----------------------------------------------------------------
// Poll.java
// Let's Meet 2021
//
// Models a poll which belongs to an event and has an array of n options

package com.LetsMeet.LetsMeet.Event.Model;

import java.util.ArrayList;
import java.util.UUID;

public class Poll {
    
    private UUID uuid;
    private UUID event;

    private String name;
    private ArrayList<String> options;

    public Poll(UUID uuid, UUID event, String name, ArrayList<String> options) {
        this.uuid = uuid;
        this.event = event;
        this.name = name;
        this.options = options;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getEvent() {
        return event;
    }

    public void setEvent(UUID event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }


}
