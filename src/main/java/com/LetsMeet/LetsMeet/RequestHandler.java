/*
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

    public static List<AdminUserData> getAllUsers(){

        UserModel model = new UserModel();
        List<AdminUserData> r = model.allUsers();
        model.close();
        return r;
    }
    // End of user methods

    // Event methods here
    public static List<AdminEventData> getAllEvents(){
        EventsModel model = new EventsModel();
        List<AdminEventData> r = model.allEvents();
        model.close();
        return r;
    }

    public static EventData getEvent(String UUID){
        EventsModel model = new EventsModel();
        EventData r = model.getEventByUUID(UUID);
        model.close();
        return r;
    }

    public static String createEvent(String name, String description, String location, String organiserUUID){
        // Get an eventUUID
        UUID uuid = EventManager.createEventUUID(name, description, location);

        // Add Event to DB
        EventsModel model = new EventsModel();
        String result = model.NewEvent(uuid.toString(), name, description, location);
        model.close();

        if(result == null){
            return "Error creating event";
        }else {
            // Add Event and user to 'HasUsers' table
            UserModel userModel = new UserModel();
            result = userModel.populateHasUsers(uuid.toString(), organiserUUID,true);
            userModel.close();
            if(result == null){
                return "Error adding organiser to event";
            }else{
                return "Event created successfully";
            }
        }
    }

    public static String deleteEvent(String EventUUID, String UserUUID){
        // Check that user is owner of event
        boolean owner = UserManager.checkIfOwner(EventUUID, UserUUID);

        if(owner){
            // Delete record from 'event' table and from 'hasusers' table
            EventsModel model = new EventsModel();
            String r = model.deleteEvent(EventUUID);
            model.close();
            return r;
        }else{
            return "You dont have permission to delete this event";
        }

    }

    public static String leaveEvent(String EventUUID, String UserUUID){
        UserModel model = new UserModel();
        String response = model.removeHasUsers(EventUUID, UserUUID);
        model.close();
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
            model.close();
            return "Error creating condition set";
        }else{
            // Add connection between Event and condition set
            result = model.AddConditionSetToEvent(EventUUID, ConditionSetUUID);
            model.close();
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
*/