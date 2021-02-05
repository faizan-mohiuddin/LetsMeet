package com.LetsMeet.LetsMeet.DBChecks;

import com.LetsMeet.LetsMeet.TestingTools.TestingEvents;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import java.sql.*;

public class EventDBChecker extends DBConnector {
    Connection con;

    public EventDBChecker(){
        super();
        this.con = super.con;
    }

    public String eventUUIDFromNameAndDesc(String name, String desc){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT Event.EventUUID FROM Event WHERE Event.Name = '%s' AND Event.Description = '%s'", name, desc);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString(1);
        }catch(Exception e){
            System.out.println("EventDBChecker: eventUUIDFromNameAndDesc");
            System.out.println(e);
            return null;
        }
    }

    public TestingEvents checkEvent(String UUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT * FROM Event WHERE Event.EventUUID = '%s'", UUID);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            TestingEvents event = new TestingEvents(rs.getString("Name"), rs.getString("Description"), rs.getString("Location"));
            event.UUID = rs.getString("EventUUID");
            return event;
        }catch(Exception e){
            System.out.println("EventDBChecker: checkEvent");
            System.out.println(e);
            return null;
        }
    }

    public void removeEventByUUID(String uuid){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM Event WHERE Event.EventUUID = '%s'", uuid);
            statement.executeUpdate(query);
        }catch(Exception e){
            System.out.println("EventDBChecker : removeEventByUUID");
            System.out.println(e);
        }
    }
}
