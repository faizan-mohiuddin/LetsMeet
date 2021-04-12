package com.LetsMeet.LetsMeet.User.Model;

import java.util.UUID;

public class IsGuest {
    UUID guestUUID;
    UUID eventUUID;

    public IsGuest(UUID guestUUID, UUID eventUUID){
        this.guestUUID = guestUUID;
        this.eventUUID = eventUUID;
    }

    public UUID getGuestUUID(){
        return this.guestUUID;
    }

    public UUID getEventUUID(){
        return this.guestUUID;
    }
}
