package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventsModel;

import java.util.UUID;

public class EventHandler {
    public void createUser(String fName, String lName, String email, String password){
        // Get a userUUID

        // Create a password hash

    }

    public static void createEvent(String name, String description, String location){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add to DB
        EventsModel model = new EventsModel();
        model.NewEvent(uuid.toString(), name, description, location);
    }

}
