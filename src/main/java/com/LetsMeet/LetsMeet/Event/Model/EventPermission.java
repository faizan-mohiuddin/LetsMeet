//-----------------------------------------------------------------
// EventPermission.java
// Let's Meet 2021
//
// Represents the permissions of a User on an Event
// Note that while this is simply a boolean isOwner property, this could be replaced with a more sophisticated "Permissions" class if required

package com.LetsMeet.LetsMeet.Event.Model;

import java.util.UUID;

public class EventPermission {
    private UUID event;
    private UUID user;

    private Boolean isOwner;

    public EventPermission(UUID event, UUID user, Boolean isOwner){
        this.event = event;
        this.user = user;
        this.isOwner = isOwner;
    }

    public EventPermission(String event, String user, Boolean isOwner){
        this.event = UUID.fromString(event);
        this.user = UUID.fromString(user);
        this.isOwner = isOwner;
    }

    public UUID getEvent(){
        return this.event;
    }

    public UUID getUser(){
        return this.user;
    }

    public Boolean getIsOwner(){
        return this.isOwner;
    }
}
