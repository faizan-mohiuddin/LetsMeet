//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on EventResponse objects
package com.LetsMeet.LetsMeet.Event.DAO;

//-----------------------------------------------------------------

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;

import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventResponseDao implements DAOconjugate<EventResponse> {

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventResponseDao.class);

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<EventResponse> get(UUID eventUUID, UUID userUUID) throws IOException {
        try(Statement statement = DatabaseInterface.get().createStatement()){
            String query = String.format("select * from EventResponse where EventResponse.EventUUID = '%s' and EventResponse.UserUUID = '%s'", eventUUID.toString(),userUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            return Optional.ofNullable(new EventResponse(
                UUID.fromString(rs.getString("EventUUID")), 
                UUID.fromString(rs.getString("UserUUID")), 
                readSerialised(rs.getBytes("EventProperties")),
                rs.getBoolean("Required")));

        }
        catch(Exception e){
            throw new IOException(e.getMessage());
        }

    }

    public Optional<List<EventResponse>> get(UUID anyUUID) throws IOException {
        try(Statement statement = DatabaseInterface.get().createStatement()){

            String query = String.format("select * from EventResponse where EventResponse.EventUUID = '%s' OR EventResponse.UserUUID = '%s'", anyUUID, anyUUID);

            ResultSet rs = statement.executeQuery(query);
            List<EventResponse> records = new ArrayList<>();

            while(rs.next())
                records.add(new EventResponse(
                    UUID.fromString(rs.getString("EventUUID")), 
                    UUID.fromString(rs.getString("UserUUID")), 
                    readSerialised(rs.getBytes("EventProperties")),
                    rs.getBoolean("Required")));

            return Optional.ofNullable(records);

        }
        catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<EventResponse>> getAll() {
        LOGGER.warn("getAll() is not implemented.");
        return Optional.empty();
    }

    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(EventResponse t) throws IOException {
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("INSERT INTO EventResponse (EventUUID, UserUUID, EventProperties, PollResponseUUID, Required) VALUES (?,?,?,?,?)")){
            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setObject(3, t.getEventProperties());
            statement.setString(4, "{}");
            statement.setBoolean(5, t.getRequired());
            int rows = statement.executeUpdate();

            if (rows > 0) return true;
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventResponse t) throws IOException {
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("UPDATE EventResponse SET EventUUID = ?, UserUUID = ?, EventProperties = ?, Required = ? WHERE EventUUID = ? AND UserUUID = ?")){

            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setObject(3, t.getEventProperties());
            statement.setBoolean(4, t.getRequired());
            statement.setString(5, t.getEvent().toString());
            statement.setString(6, t.getUser().toString());

            if(statement.executeUpdate() > 0)return true;
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(EventResponse t) throws IOException {
        try(Statement statement = DatabaseInterface.get().createStatement()){
            String query = String.format("DELETE FROM EventResponse WHERE EventResponse.EventUUID = '%s' AND EventResponse.UserUUID = '%s'",
                    t.getEvent().toString(),t.getUser().toString());
            int rows = statement.executeUpdate(query);

            if(rows > 0) return true;
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean delete(String eventUUID, String userUUID) throws IOException {
        return delete(get(UUID.fromString(eventUUID), UUID.fromString(userUUID)).get());
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
