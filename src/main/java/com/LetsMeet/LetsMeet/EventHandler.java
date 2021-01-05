package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.EventsModel;
import com.LetsMeet.Models.UserModel;
import jdk.jfr.Event;
import org.apache.catalina.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class EventHandler {

    // User methods here
    public static String createUser(String fName, String lName, String email, String password){
        // Get a userUUID
        UUID uuid = UserManager.createUserUUID(fName, lName, email);

        UserManager manager = new UserManager();

        // Create a salt
        byte[] salt = manager.generateSalt();

        // Create a password hash
        byte[] hash = manager.generateHash(password, salt);

        if(hash == null){
            return "An error occured creating account";
        }else{
            // Convert hash and salt to hex
            String HexHash = manager.toHex(hash);
            String HexSalt = manager.toHex(salt);
            
            // Add to DB
            UserModel model = new UserModel();
            return model.newUser(uuid.toString(), fName, lName, email, HexHash, HexSalt);
        }

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
