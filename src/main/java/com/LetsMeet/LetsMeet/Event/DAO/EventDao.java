//-----------------------------------------------------------------
// EventDAO.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on Event objects/records

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
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventDao implements DAO<Event> {

    // Components
    //-----------------------------------------------------------------
    @Autowired
    DBConnector database;

    @Autowired
    EventPermissionDao hasUsers;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<Event> get(UUID uuid) {
        return get(uuid.toString());
    }

    public Optional<Event> get(String uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from Event where Event.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<Event> response = Optional.ofNullable(new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),UUID.fromString(rs.getString(5))));
            database.close();
            return response;

        }catch(Exception e){
            database.close();
            System.out.println("\nEvent Dao: get (String)");
            System.out.println(e);
            return Optional.empty();
            
        }
    }

    @Override
    public Optional<Collection<Event>> getAll() {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            ResultSet rs = statement.executeQuery("select * from Event");
            List<Event> events = new ArrayList<>();

            while (rs.next()){
                events.add(new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),UUID.fromString(rs.getString(5))));
            }
            database.close();
            return Optional.ofNullable(events);

        }catch(Exception e){
            System.out.println("\nEvent Dao: getALL");
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
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location, ConditionSet, Poll) VALUES (?,?,?,?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setString(3, t.getDescription());
            statement.setString(4, t.getLocation());
            statement.setString(5, t.getConditions().toString());
            statement.setString(6, "{}");

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Event Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }

    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(Event t) {
        database.open();
        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("UPDATE Event SET Name = ?, Description = ?, Location = ?, ConditionSet = ?, Poll = ? WHERE EventUUID = ?")){

            statement.setString(1, t.getName());
            statement.setString(2, t.getDescription());
            statement.setString(3, t.getLocation());
            statement.setString(4, t.getConditions().toString());
            statement.setString(5, "{}");
            statement.setString(6, t.getUUID().toString());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Event Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }
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
        try(Statement statement = database.con.createStatement()){
        
            String query;
            String eventUUID = uuid.toString();

            query = String.format("DELETE FROM Event where Event.EventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            query = String.format("DELETE FROM HasUsers where HasUsers.eventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return true;

        }catch(Exception e){
            System.out.println("Event Dao: delete (UUID)");
            database.close();
            e.printStackTrace();
            return false;
        }
    }




    // Other methods
    //-----------------------------------------------------------------
    //TODO EventResponse should be be loaded by BL to find which events a user is registered to.
    public Optional<Collection<Event>> getUserEvents(String uuid){
        database.open();
        try(Statement statement = database.con.createStatement()){
            List<EventPermission> records = hasUsers.get(uuid).get();

            database.open();

            List<Event> events = new ArrayList<>();

            for(EventPermission record: records){
                String query = String.format("select Event.EventUUID, Event.Name, Event.Description, Event.Location, Event.ConditionSet" +
                        " from Event where Event.EventUUID = '%s'", record.getEvent().toString());

                ResultSet rs = statement.executeQuery(query);
                rs.next();
                Event event = new Event(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),UUID.fromString(rs.getString(5)));
                events.add(event);
            }

            Optional<Collection<Event>> response = Optional.ofNullable(events);
            database.close();
             return response;

        }catch(Exception e){
            System.out.println("\nEvent Dao : get user events");
            e.printStackTrace();
            database.close();
            return Optional.empty();
        }
    }
}
