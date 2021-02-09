package com.LetsMeet.LetsMeet.DBChecks;

import com.LetsMeet.LetsMeet.TestingTools.TestingEvents;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class EventDBChecker{

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DBConnector database;

    public String eventUUIDFromNameAndDesc(String name, String desc){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT Event.EventUUID FROM Event WHERE Event.Name = '%s' AND Event.Description = '%s'", name, desc);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            database.close();
            return rs.getString(1);
        }catch(Exception e){
            database.close();
            System.out.println("EventDBChecker: eventUUIDFromNameAndDesc");
            System.out.println(e);
            return null;
        }
    }

    public TestingEvents checkEvent(String UUID){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT * FROM Event WHERE Event.EventUUID = '%s'", UUID);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            TestingEvents event = new TestingEvents(rs.getString("Name"), rs.getString("Description"), rs.getString("Location"));
            event.UUID = rs.getString("EventUUID");
            database.close();
            return event;
        }catch(Exception e){
            database.close();
            System.out.println("EventDBChecker: checkEvent");
            System.out.println(e);
            return null;
        }
    }

    public void removeEventByUUID(String uuid){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("DELETE FROM Event WHERE Event.EventUUID = '%s'", uuid);
            statement.executeUpdate(query);
            database.close();
        }catch(Exception e){
            database.close();
            System.out.println("EventDBChecker : removeEventByUUID");
            System.out.println(e);
        }
    }
}
