package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.*;
import org.apache.catalina.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EventHandler {

    // User methods here
    public static String createUser(String fName, String lName, String email, String password){
        // Verify that email address has not already been used
        boolean uniqueEmail = UserManager.checkUniqueEmail(email);

        if(uniqueEmail) {
            // Get a userUUID
            UUID uuid = UserManager.createUserUUID(fName, lName, email);

            UserManager manager = new UserManager();

            // Create a salt
            byte[] salt = manager.generateSalt();

            // Create a password hash
            byte[] hash = manager.generateHash(password, salt);

            if (hash == null) {
                return "An error occured creating account";
            } else {
                // Convert hash and salt to hex
                String HexHash = manager.toHex(hash);
                String HexSalt = manager.toHex(salt);

                // Add to DB
                UserModel model = new UserModel();
                return model.newUser(uuid.toString(), fName, lName, email, HexHash, HexSalt);
            }
        }else{
            return "Email address is already used for another account.";
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

    public static boolean checkValidAPIToken(String token){
        // Get record from DB
        UserModel model = new UserModel();
        TokenData tokenData = model.getTokenRecord(token);

        if(tokenData == null){
            return false;
        }else{
            // Check token expiry
            long currentTime = Instant.now().getEpochSecond();
            if(currentTime <= tokenData.getExpires()){
                return true;
            }else{
                return false;
            }
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

    public static String createEvent(String name, String description, String location, String organiserUUID){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add Event to DB
        EventsModel model = new EventsModel();
        String result = model.NewEvent(uuid.toString(), name, description, location);

        if(result == null){
            return "Error creating event";
        }else {
            // Add Event and user to 'HasUsers' table
            UserModel userModel = new UserModel();
            result = userModel.populateHasUsers(uuid.toString(), organiserUUID,true);
            if(result == null){
                return "Error adding organiser to event";
            }else{
                return "Event created successfully";
            }
        }
    }

    public static String joinEvent(String EventUUID, String UserUUID){
        // Check event exists
        EventsModel model = new EventsModel();
        EventData data = model.getEventByUUID(EventUUID);

        if(data == null){
            return "Event Doesnt exist";
        }

        // Add user to event as not an owner
        UserModel userModel = new UserModel();
        String result = userModel.populateHasUsers(EventUUID, UserUUID,false);
        if(result == null){
            return "Error adding user to event";
        }else{
            return "User added to event";
        }
    }
    // End of event methods
}
