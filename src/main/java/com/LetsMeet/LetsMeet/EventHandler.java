package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.EventsModel;
import jdk.jfr.Event;

import java.util.List;
import java.util.UUID;

public class EventHandler {

    // User methods here
    public void createUser(String fName, String lName, String email, String password){
        // Get a userUUID
        UUID uuid = UserManager.createUserUUID(fName, lName, email);

        // Create a password hash

        // Add to DB

    }
    // End of user methods

    // Event methods here
    public static List<EventData> getAllEvents(){
        EventsModel model = new EventsModel();
        return model.allEvents();
    }

    public static EventData getEvent(String UUID){
        EventsModel model = new EventsModel();
        return model.getEventByUUID(UUID);
    }

    public static void createEvent(String name, String description, String location){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add to DB
        EventsModel model = new EventsModel();
        model.NewEvent(uuid.toString(), name, description, location);
    }
    // End of event methods
}
