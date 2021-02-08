//-----------------------------------------------------------------
// EventDAO.java
// Let's Meet 2021
//
// Responsible for perfoming CRUD operations on Event objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

//-----------------------------------------------------------------

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import com.LetsMeet.Models.EventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventDao implements DAO<Event> {

    // Components
    //-----------------------------------------------------------------
    @Autowired
    DBConnector database;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<Event> get(UUID uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from Event where Event.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            database.close();
            return Optional.ofNullable(new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));

        }catch(Exception e){
            database.close();
            e.printStackTrace();
            return Optional.empty();
        }  
    }

    public Optional<Event> get(String uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from Event where Event.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            database.close();
            return Optional.ofNullable(new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));

        }catch(Exception e){
            database.close();
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<Event>> getAll() {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            ResultSet rs = statement.executeQuery("select * from User");
            List<Event> events = new ArrayList<>();

            while (rs.next()){
                events.add(new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            database.close();
            return Optional.ofNullable(events);

        }catch(Exception e){
            database.close();
            e.printStackTrace();
            return Optional.empty();
        }  
    }


    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(Event t) {
        database.open();


        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location) VALUES (?,?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setString(3, t.getDescription());
            statement.setString(4, t.getLocation());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            database.close();
            e.printStackTrace();
            return false;
        }

    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(Event t) {
        // TODO Auto-generated method stub
        return false;
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(Event t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(UUID uuid) {
        database.open();
        try{
            Statement statement = database.con.createStatement();

            String query;
            String eventUUID = uuid.toString();

            query = String.format("DELETE FROM HasUsers where HasUsers.eventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            query = String.format("DELETE FROM Event where Event.EventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return true;

        }catch(Exception e){
            database.close();
            System.out.println("\nEvent Dao: delete (UUID)");
            e.printStackTrace();
            return false;
        }
    }

    // Has Users Table Helpers
    //-----------------------------------------------------------------


    // Other methods
    //-----------------------------------------------------------------

    public Optional<Collection<Event>> getUserEvents(String uuid){
        database.open();
        try(Statement statement = database.con.createStatement()){
            String query = String.format("select Event.EventUUID, Event.Name, Event.Description, Event.Location" +
                    " from Event, HasUsers " +
                    "where HasUsers.UserUUID = '%s' and Event.EventUUID = HasUsers.EventUUID", uuid);

            ResultSet rs = statement.executeQuery(query);
            List<Event> events = new ArrayList<>();
            while(rs.next()){
                Event event = new Event(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
                events.add(event);
            }

            database.close();
            return Optional.ofNullable(events);

        }catch(Exception e){
            e.printStackTrace();
            database.close();
            return Optional.empty();
        }
    }
}
