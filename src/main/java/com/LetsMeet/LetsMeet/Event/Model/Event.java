//-----------------------------------------------------------------
// Event.java
// Let's Meet 2021
//
// Models an Event. Event has a ConditionSet which contains any constraints/domain values which belong to the event.

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//-----------------------------------------------------------------

@JsonPropertyOrder({ "name", "description", "location" })
public class Event {
    UUID uuid;
    String name;
    String desc;
    String location;

    // The events own specific set of conditions and constraints (not those belonging to a user)
    UUID conditionSet;

    public Event(String uuid, String name, String desc, String location, UUID conditions){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.conditionSet = conditions;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.desc;
    }

    public String getLocation(){
        return this.location;
    }

    public UUID getConditions(){
        return this.conditionSet;
    }

    public EventSanitised convertToSanitised(){
        return new EventSanitised(this.name, this.desc, this.location);
    }
}
