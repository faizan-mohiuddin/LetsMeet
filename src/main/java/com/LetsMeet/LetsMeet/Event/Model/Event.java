//-----------------------------------------------------------------
// Event.java
// Let's Meet 2021
//
// Models an Event. Event has a ConditionSet which contains any constraints/domain values which belong to the event.

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//-----------------------------------------------------------------

@JsonPropertyOrder({ "name", "description", "location" })
public class Event {
    UUID uuid;
    String name;
    String desc;
    String location;
    EntityProperties properties;

    // The events own specific set of conditions and constraints (not those belonging to a user)
    UUID conditionSet;
    Poll poll;

    public Event(String uuid, String name, String desc, String location, EntityProperties properties, UUID conditions, Poll poll){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.properties = properties;
        this.conditionSet = conditions;
        this.poll = poll;
    }

    public Event(String uuid, String name, String desc, String location, UUID conditions){
        this(uuid, name, desc, location, new EntityProperties(), conditions, new Poll());
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

    public EntityProperties getProperties() {
        return properties;
    }

    public void setProperties(EntityProperties properties) {
        this.properties = properties;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String desc){
        this.desc = desc;
    }

    public void setLocation(String location){
        this.location = location;
    }
}
