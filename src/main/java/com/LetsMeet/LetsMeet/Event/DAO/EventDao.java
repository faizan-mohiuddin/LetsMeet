//-----------------------------------------------------------------
// EventDAO.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on Event objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventDao implements DAO<Event> {

    // Components
    //-----------------------------------------------------------------
    private static final Logger LOGGER= LoggerFactory.getLogger(EventDao.class);

    @Autowired
    EventPermissionDao hasUsers;

    @Autowired
    ConnectionService connectionService;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<Event> get(UUID uuid) throws IOException {
        return get(uuid.toString());
    }

    public Optional<Event> get(String uuid) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("select * from Event where Event.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<Event> event = Optional.ofNullable(new Event(
                UUID.fromString(rs.getString("EventUUID")),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getString("Location"),
                new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                readSerialised(rs.getBytes("EventProperties")),
                new Gson().fromJson(rs.getString("Poll"), Poll.class)));

            
            return event;

        }catch(SQLException e){

            if (e.getSQLState().contains("S1000"))
                return Optional.empty();
        
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<Event>> getAll() throws IOException{
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            ResultSet rs = statement.executeQuery("select * from Event");
            List<Event> events = new ArrayList<>();

            while (rs.next()){
                events.add(new Event(
                    UUID.fromString(rs.getString("EventUUID")),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                    readSerialised(rs.getBytes("EventProperties")),
                    new Gson().fromJson(rs.getString("Poll"), Poll.class)));
            }

            
            return Optional.ofNullable(events);

        }catch(SQLException e){
            
            throw new IOException(e.getMessage());
        }  
    }

    public Optional<List<Event>> search(String query){
        LOGGER.debug("Event Search: {}", query);
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            ResultSet rs = statement.executeQuery(query);

            List<Event> events = new ArrayList<>();

            while (rs.next()) {
                events.add(new Event(
                        UUID.fromString(rs.getString("EventUUID")),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                        readSerialised(rs.getBytes("EventProperties")),
                        new Gson().fromJson(rs.getString("Poll"), Poll.class)));
            }
            return Optional.of(events);

        }catch(Exception e){
            return Optional.empty();
        }
    }


    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(Event t) throws IOException {

        // Save the event
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location, EventProperties, Poll, EntityProperties) VALUES (?,?,?,?,?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setString(3, t.getDescription());
            statement.setString(4, t.getLocation());
            statement.setObject(5, t.getEventProperties());
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
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("UPDATE Event SET Name = ?, " +
                "Description = ?, Location = ?, EventProperties = ?, Poll = ?, EntityProperties = ? WHERE EventUUID = ?")){

            statement.setString(1, t.getName());
            statement.setString(2, t.getDescription());
            statement.setString(3, t.getLocation());
            statement.setObject(4, t.getEventProperties());
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
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
        
            String query;
            String eventUUID = uuid.toString();

            query = String.format("DELETE FROM Event where Event.EventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            query = String.format("DELETE FROM HasUsers where HasUsers.eventUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return true;

        }catch(SQLException e){
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    private EventProperties readSerialised(byte[] buf){
        try{
            ObjectInputStream objectIn = null;

            // If bytes are present, try to deserialize
            if (buf != null){

                objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                Object object = objectIn.readObject();

                // Attempt to cast object to ConditionSet - is there a better way to do this?
                if (object instanceof EventProperties){
                    return (EventProperties) object;  
                }
                
            }
            return EventProperties.getEmpty();
        }
        catch(Exception e){
            return EventProperties.getEmpty();
        }
    }
}
