//-----------------------------------------------------------------
// EventDAO.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on Event objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//-----------------------------------------------------------------

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.Poll;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;
import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventDao implements DAO<Event> {

    // Components
    //-----------------------------------------------------------------


    @Autowired
    EventPermissionDao hasUsers;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<Event> get(UUID uuid) throws IOException {
        return get(uuid.toString());
    }

    public Optional<Event> get(String uuid) throws IOException {
        try(Statement statement = DatabaseInterface.get().createStatement()) {
            String query = String.format("select * from Event where Event.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<Event> event = Optional.ofNullable(new Event(
                UUID.fromString(rs.getString("EventUUID")),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getString("Location"),
                new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                new Gson().fromJson(rs.getString("EventProperties"), EventProperties.class),
                new Gson().fromJson(rs.getString("Poll"), Poll.class)));

            DatabaseInterface.drop();
            return event;

        }catch(SQLException e){
            DatabaseInterface.drop();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<Event>> getAll() throws IOException{
        try(Statement statement = DatabaseInterface.get().createStatement()){
            ResultSet rs = statement.executeQuery("select * from Event");
            List<Event> events = new ArrayList<>();

            while (rs.next()){
                events.add(new Event(
                    UUID.fromString(rs.getString("EventUUID")),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                    new Gson().fromJson(rs.getString("EventProperties"), EventProperties.class),
                    new Gson().fromJson(rs.getString("Poll"), Poll.class)));
            }

            DatabaseInterface.drop();
            return Optional.ofNullable(events);

        }catch(SQLException e){
            DatabaseInterface.drop();
            throw new IOException(e.getMessage());
        }  
    }


    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(Event t) throws IOException {

        // Save the event
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location, ConditionSet, Poll, EntityProperties) VALUES (?,?,?,?,?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setString(3, t.getDescription());
            statement.setString(4, t.getLocation());
            statement.setString(5, new Gson().toJson(t.getEventProperties()));
            statement.setString(6, new Gson().toJson(t.getPoll()));
            statement.setString(7, new Gson().toJson(t.getProperties()));

            if(statement.executeUpdate() > 0){return true;}
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }

    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(Event t) throws IOException{
        // Save the event
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("UPDATE Event SET Name = ?, " +
                "Description = ?, Location = ?, EventProperties = ?, Poll = ?, EntityProperties = ? WHERE EventUUID = ?")){

            statement.setString(1, t.getName());
            statement.setString(2, t.getDescription());
            statement.setString(3, t.getLocation());
            statement.setString(4, new Gson().toJson(t.getEventProperties()));
            statement.setString(5, new Gson().toJson(t.getPoll()));
            statement.setString(6, new Gson().toJson(t.getProperties()));
            statement.setString(7, t.getUUID().toString());

            if(statement.executeUpdate() > 0) return true;      
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(Event event) throws IOException {
        return delete(event.getUUID());
    }

    @Override
    public Boolean delete(UUID uuid) throws IOException{
        try(Statement statement = DatabaseInterface.get().createStatement()){
        
            String query;
            String eventUUID = uuid.toString();

            query = String.format("DELETE FROM Event where Event.EventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            query = String.format("DELETE FROM HasUsers where HasUsers.eventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return true;

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }
}
