//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on EventPermission objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

//-----------------------------------------------------------------

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Poll;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;

import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventPermissionDao implements DAOconjugate<EventPermission> {

    // Get logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventPermissionDao.class);

    @Autowired
    ConnectionService connectionService;
    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<EventPermission> get(UUID event, UUID user) {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s' and HasUsers.UserUUID = '%s'", event.toString(),user.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<EventPermission> response = Optional.ofNullable(new EventPermission(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)), rs.getBoolean(3)));
            return response;
        }
        catch(Exception e){
            LOGGER.error("Failed to get EventPermission: {} ", e.getMessage());
            return Optional.empty();
        }
    }

    // Get EventPermission accepts either EventUUID or UserUUID (as either *should* be unique)
    public Optional<List<EventPermission>> get(String uuid) {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s' OR HasUsers.UserUUID = '%s'", uuid, uuid);

            ResultSet rs = statement.executeQuery(query);
            List<EventPermission> records = new ArrayList<>();
            while(rs.next()){
                EventPermission record = new EventPermission(rs.getString(1), rs.getString(2), rs.getBoolean(3));
                records.add(record);
            }
            return Optional.ofNullable(records);

        }
        catch(Exception e){
            LOGGER.error("Failed to get EventPermission: {} ", e.getMessage());
            return Optional.empty();
        }
    }

    public Map<EventPermission, Event> getWithEvent(UUID anyUUID) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement()){

            String query = String.format("select * from HasUsers INNER JOIN Event ON HasUsers.EventUUID = Event.EventUUID where Event.EventUUID='%s' OR HasUsers.UserUUID='%s'", anyUUID, anyUUID);
            Instant time1 = Instant.now();
            ResultSet rs = statement.executeQuery(query);

            Map<EventPermission, Event> records = new HashMap<>();

            while(rs.next()){
                Event event = new Event(
                        UUID.fromString(rs.getString("EventUUID")),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        new Gson().fromJson(rs.getString("EntityProperties"), EntityProperties.class),
                        EventDao.readSerialised(rs.getBytes("EventProperties")),
                        new Gson().fromJson(rs.getString("Poll"), Poll.class));

                EventPermission perm = new EventPermission(rs.getString("EventUUID"), rs.getString("UserUUID"), rs.getBoolean("IsOwner"));

                records.put(perm,event);
            }
            Instant time2 = Instant.now();
            long resMilli = Duration.between(time1, time2).toMillis();
            System.out.println("Duration to query: "+resMilli);
            return records;
        }
        catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<EventPermission>> getAll() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    // Create
    //-----------------------------------------------------------------

    @Override
    public Boolean save(EventPermission t) {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO HasUsers (EventUUID, UserUUID, IsOwner) VALUES (?,?,?)")){
            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner());
            int rows = statement.executeUpdate();

            return (rows > 0)? true : false;
            
        }
        catch(Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            return false;
        }
    }


    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventPermission t) {

        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("UPDATE HasUsers SET EventUUID = ?, UserUUID = ?, IsOwner = ? WHERE EventUUID = ? AND UserUUID = ?")){

            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner());
            statement.setString(4, t.getEvent().toString());
            statement.setString(5, t.getUser().toString());

            if(statement.executeUpdate() > 0){
                return true;
            }
            else
                throw new SQLException("UPDATE on HasUsers failed");

        }catch(Exception e){
            LOGGER.error("Unable to update: {}", e.getMessage());
            return false;
        }
    }


    // Delete
    //-----------------------------------------------------------------


    @Override
    public Boolean delete(EventPermission t) {
        return this.delete(t.getEvent().toString(), t.getUser().toString());
    }

    @Override
    public Boolean delete(String EventUUID, String UserUUID) {

        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            String query = String.format("DELETE FROM HasUsers WHERE HasUsers.EventUUID = '%s' AND HasUsers.UserUUID = '%s'",
                    EventUUID, UserUUID);
            int rows = statement.executeUpdate(query);

            return (rows > 0)? true : false;

        }catch(Exception e){
            LOGGER.error("Failed to delete EventPermission: {} ", e.getMessage());
            return false;
        }
    }
    
}
