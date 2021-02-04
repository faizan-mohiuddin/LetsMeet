package com.LetsMeet.Models.Connectors;

import com.LetsMeet.Models.Data.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsModel extends DBConnector {

    Connection con;

    public EventsModel(){
        super();
        this.con = super.con;
    }


    public String NewEvent(String UUID, String name, String desc, String location ){
        try{
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location) VALUES (?,?,?,?)");
            statement.setString(1, UUID);
            statement.setString(2, name);
            statement.setString(3, desc);
            statement.setString(4, location);

            int rows = statement.executeUpdate();

            if(rows > 0){
                return "Event Created Successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("\nEvents Model: NewEvent");
            System.out.println(e);
            return null;
        }
    }

    public List<AdminEventData> allEvents(){
        try{
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("select * from Event");

            List<AdminEventData> events = new ArrayList<>();

            while(rs.next()){
                AdminEventData event = new AdminEventData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                events.add(event);
            }
            return events;

        }catch(Exception e){
            System.out.println("\nEvents Model: allEvents");
            System.out.println(e);
            return null;
        }
    }

    public EventData getEventByUUID(String UUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from Event where Event.EventUUID = '%s'", UUID);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            EventData event = new EventData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return event;

        }catch(Exception e){
            if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                System.out.println("\nEvents Model: getEventByUUID");
                System.out.println(e);
            }
            return null;
        }
    }

    public String UserUUIDFromToken(String token){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select UserUUID from Token where Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString(1);
        }catch(Exception e){
            System.out.println("\nEvents Model: UserUUIDFromToken");
            System.out.println(e);
            return null;
        }
    }

    public List<HasUsersRecord> getHasUsers(String eventUUID, String UserUUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s' and HasUsers.UserUUID = '%s' ", eventUUID, UserUUID);

            ResultSet rs = statement.executeQuery(query);
            List<HasUsersRecord> records = new ArrayList<>();

            while(rs.next()){
                HasUsersRecord record = new HasUsersRecord();
                record.populate(rs.getString(1), rs.getString(2), rs.getBoolean(3));
                records.add(record);
            }
            return records;
        }catch(Exception e){
            System.out.println("\nEvents Model: getHasUsers");
            System.out.println(e);
            return null;
        }
    }

    public List<HasUsersRecord> getHasUsers(String eventUUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s'", eventUUID);

            ResultSet rs = statement.executeQuery(query);
            List<HasUsersRecord> records = new ArrayList<>();

            while(rs.next()){
                HasUsersRecord record = new HasUsersRecord();
                record.populate(rs.getString(1), rs.getString(2), rs.getBoolean(3));
                records.add(record);
            }
            return records;
        }catch(Exception e){
            System.out.println("\nEvents Model: getHasUsers");
            System.out.println(e);
            return null;
        }
    }

    public String deleteEvent(String eventUUID){
        try{
            Statement statement = this.con.createStatement();

            String query;

            query = String.format("DELETE FROM HasUsers where HasUsers.eventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            query = String.format("DELETE FROM Event where Event.EventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return "Event successfully deleted.";

        }catch(Exception e){
            System.out.println("\nEvents Model: deleteEvent");
            System.out.println(e);
            return "Error deleting event";
        }
    }

    public List<EventData> getEventsByUserUUID(String UUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select Event.EventUUID, Event.Name, Event.Description, Event.Location" +
                    " from Event, HasUsers " +
                    "where HasUsers.UserUUID = '%s' and Event.EventUUID = HasUsers.EventUUID", UUID);

            ResultSet rs = statement.executeQuery(query);
            List<EventData> events = new ArrayList<>();
            while(rs.next()){
                EventData event = new EventData(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
                events.add(event);
            }
            return events;
        }catch(Exception e){
            System.out.println("\nEvents Model: getEventsByUserUUID");
            System.out.println(e);
            return null;
        }
    }

    // Space for condition sets ///////////////////////////////////////////////////////////////////////////////////////
    public String NewConditionSet(String ConditionSetUUID, String Name, String UserUUID){
        try{
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO ConditionSet (ConditionSetUUID, Name, UserUUID) VALUES (?,?,?)");
            statement.setString(1, ConditionSetUUID);
            statement.setString(2, Name);
            statement.setString(3, UserUUID);

            int rows = statement.executeUpdate();

            if(rows > 0){
                return "ConditionSet created successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public String AddConditionSetToEvent(String EventUUID, String ConditionSetUUID){
        try{
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO HasConditionSet (ConditionSetUUID, EventUUID) VALUES (?,?)");
            statement.setString(1, ConditionSetUUID);
            statement.setString(2, EventUUID);

            int rows = statement.executeUpdate();

            if(rows > 0){
                return "ConditionSet added to Event successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    // Load a condition set from storage.
    // TODO implement methods for fetching variables and conditions before replacing dummy data
    public ConditionSet getConditionSetByUUID(String ConditionSetUUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from ConditionSet where ConditionSet.ConditionSetUUID = '%s'", ConditionSetUUID);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            // Generate dummy data
            String uuid = "00000000-0000-0000-0000-000000000000";
            Integer[] nums = {1,2,3,4};
            Variable<Integer> var1 = new Variable<Integer>(uuid, "var1", nums);
            Variable<Integer> var2 = new Variable<Integer>(uuid, "var2", nums);
            Constraint<Integer> con1 = new Constraint<Integer>(uuid, "con1", var1, var2, '=');
            Variable<?>[] varArray = {var1,var2};
            Constraint<?>[] conArray = {con1};

            return new ConditionSet(rs.getString(1),rs.getString(2),varArray,conArray);

        }catch(Exception e){
            System.out.println("\nEvents Model: getEventByUUID");
            System.out.println(e);
            return null;
        }
    }

    // End of space for condition sets ////////////////////////////////////////////////////////////////////////////////
}
