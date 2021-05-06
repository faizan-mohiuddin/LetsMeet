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
    EventProperties eventProperties;
    Poll poll;

    public Event(UUID uuid, String name, String desc, String location, EntityProperties properties, EventProperties eventProperties, Poll poll){
        this.uuid = uuid;
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.properties = properties;
        this.eventProperties = eventProperties;
        this.poll = poll;
    }

    public Event(UUID uuid, String name, String desc, String location, EventProperties eventProperties){
        this(uuid, name, desc, location, new EntityProperties(), eventProperties, new Poll());
    }

    public Event(String name){
        this(UUID.randomUUID(), name, "", "", EventProperties.getEmpty());
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
        if(!name.equals("")) {
            this.name = name;
        }
    }

    public void setDescription(String desc){
        if(!desc.equals("")) {
            this.desc = desc;
        }
    }

    public void setLocation(String location){
        if(!location.equals("")) {
            this.location = location;
        }
    }

    public EventProperties getEventProperties() {
        return eventProperties;
    }

    public void setEventProperties(EventProperties eventProperties) {
        this.eventProperties = eventProperties;
    }
}
