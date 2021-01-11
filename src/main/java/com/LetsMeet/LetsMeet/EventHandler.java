package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.EventData;
import com.LetsMeet.Models.EventsModel;
import com.LetsMeet.Models.UserData;
import com.LetsMeet.Models.UserModel;
import java.time.Instant;
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

    public static UserData validate(String email, String password){
        UserModel model = new UserModel();

        // Get user record corresponding to email
        UserData user = model.getUserByEmail(email);

        // Check is password is correct
        boolean match = UserManager.validatePassword(password, user.getPasswordHash(), user.getSalt());

        if(match) {
            return user;
        }else{
            return null;
        }
    }

    public static String getUserToken(UserData user){
        // User needs new token issued
        // Check if user currently has a token issued
        UserModel model = new UserModel();

        // If they do, remove it and issue a new one
        if(model.CheckUserToken(user.getUserUUID())){
            // Remove tokens
            model.removeAllUserToken(user.getUserUUID());
        }

        // Create new token
        String token = UserManager.createAPItoken(user.getUserUUID(), user.getfName(), user.getlName(),
                user.getEmail(), user.getSalt());

        // Add to DB
        long tokenExpires = Instant.now().getEpochSecond() + 3600;  // Token expires an hour from when it was created
        String feedback = model.createToken(user.getUserUUID(), token, tokenExpires);

        if(feedback.equals("Token created successfully")) {
            return token;
        }else{
            return feedback;
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

    public static String createEvent(String name, String description, String location, UserData organiser){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add to DB
        EventsModel model = new EventsModel();
        return model.NewEvent(uuid.toString(), name, description, location);
    }
    // End of event methods
}
