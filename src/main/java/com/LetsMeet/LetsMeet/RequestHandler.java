package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.*;
import jdk.jfr.Event;
import org.apache.catalina.User;
import org.apache.catalina.valves.rewrite.RewriteCond.Condition;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestHandler {

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
                String r = model.newUser(uuid.toString(), fName, lName, email, HexHash, HexSalt);
                model.closeCon();
                return r;
            }
        }else{
            return "Email address is already used for another account.";
        }

    }

    public static UserData validate(String email, String password){
        UserModel model = new UserModel();

        // Get user record corresponding to email
        UserData user = model.getUserByEmail(email);
        model.closeCon();

        // Check is password is correct
        boolean match = UserManager.validatePassword(password, user);

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
        if(model.CheckUserToken(user.whatsUUID())){
            // Remove tokens
            model.removeAllUserToken(user.whatsUUID());
        }

        // Create new token
        String token = UserManager.createAPItoken(user.whatsUUID(), user.getfName(), user.getlName(),
                user.getEmail(), user.whatsSalt());

        // Add to DB
        long tokenExpires = Instant.now().getEpochSecond() + 3600;  // Token expires an hour from when it was created
        String feedback = model.createToken(user.whatsUUID(), token, tokenExpires);
        model.closeCon();

        if(feedback.equals("Token created successfully")) {
            return token;
        }else{
            return feedback;
        }
    }

    public static String getUserUUIDfromToken(String token){
        UserModel model = new UserModel();
        String userUUID = model.getUserUUIDByToken(token);
        model.closeCon();
        return userUUID;
    }

    public static String deleteUser(String UserUUID){
        // Delete events where user is owner
        List<EventData> events = RequestHandler.getMyEvents(UserUUID);
        String r;

        for(EventData event : events){
            r = RequestHandler.deleteEvent(event.whatsUUID().toString(), UserUUID);
            if(r.equals("Error deleting event")){
                // Stop
                return "Error Deleting user";
            }
        }

        // Remove user from user table
        UserModel model = new UserModel();
        r = model.deleteUser(UserUUID);
        model.closeCon();
        return r;
    }

    public static UserData getUserFromToken(String token){
        return UserManager.getUserFromToken(token);
    }

    public static List<AdminUserData> getAllUsers(){

        UserModel model = new UserModel();
        List<AdminUserData> r = model.allUsers();
        model.closeCon();
        return r;
    }
    // End of user methods

    // Event methods here
    public static List<AdminEventData> getAllEvents(){
        EventsModel model = new EventsModel();
        List<AdminEventData> r = model.allEvents();
        model.closeCon();
        return r;
    }

    public static List<EventData> getMyEvents(String UserUUID){
        EventsModel model = new EventsModel();
        List<EventData> events = model.getEventsByUserUUID(UserUUID);
        model.closeCon();
        return events;
    }

    public static EventData getEvent(String UUID){
        EventsModel model = new EventsModel();
        EventData r = model.getEventByUUID(UUID);
        model.closeCon();
        return r;
    }

    public static String createEvent(String name, String description, String location, String organiserUUID){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add Event to DB
        EventsModel model = new EventsModel();
        String result = model.NewEvent(uuid.toString(), name, description, location);
        model.closeCon();

        if(result == null){
            return "Error creating event";
        }else {
            // Add Event and user to 'HasUsers' table
            UserModel userModel = new UserModel();
            result = userModel.populateHasUsers(uuid.toString(), organiserUUID,true);
            userModel.closeCon();
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

    public static String deleteEvent(String EventUUID, String UserUUID){
        // Check that user is owner of event
        boolean owner = UserManager.checkIfOwner(EventUUID, UserUUID);

        if(owner){
            // Delete record from 'event' table and from 'hasusers' table
            EventsModel model = new EventsModel();
            String r = model.deleteEvent(EventUUID);
            model.closeCon();
            return r;
        }else{
            return "You dont have permission to delete this event";
        }

    }

    public static String leaveEvent(String EventUUID, String UserUUID){
        UserModel model = new UserModel();
        String response = model.removeHasUsers(EventUUID, UserUUID);
        model.closeCon();
        return response;
    }
    // End of event methods

    // ConditionSet Methods
    public static String NewConditionSet(String EventUUID, String UserUUID, String SetName){
        // Create ConditionSetUUID
        String ConditionSetUUID = EventManager.createConditionSetUUID(EventUUID, UserUUID, SetName).toString();

        // Add conditionSet to DB
        EventsModel model = new EventsModel();
        String result = model.NewConditionSet(ConditionSetUUID, SetName, UserUUID);
        if(result == null){
            model.closeCon();
            return "Error creating condition set";
        }else{
            // Add connection between Event and condition set
            result = model.AddConditionSetToEvent(EventUUID, ConditionSetUUID);
            model.closeCon();
            if(result == null){
                return "Error adding condition set to event";
            }else{
                return "Condition set successfully created";
            }
        }

    }

    public static String AddVariableToConditionSet(String ConditionSetUUID, Variable<?> variable){
        ConditionSetManager manager = new ConditionSetManager(ConditionSetUUID);
        manager.addVariable(variable);
        return "not implimented";
    }

    public static String AddConstraintToConditionSet(String ConditionSetUUID, Constraint<?> constraint){
        ConditionSetManager manager = new ConditionSetManager(ConditionSetUUID);
        manager.addConstraint(constraint);
        return "not implimented";
    }
    // End of ConditionSet Methods
}
