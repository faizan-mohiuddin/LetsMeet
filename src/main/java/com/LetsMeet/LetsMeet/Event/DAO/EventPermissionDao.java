//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on EventPermission objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

//-----------------------------------------------------------------

import java.util.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

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
    DBConnector database;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<EventPermission> get(UUID event, UUID user) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s' and HasUsers.UserUUID = '%s'", event.toString(),user.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<EventPermission> response = Optional.ofNullable(new EventPermission(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)), rs.getBoolean(3)));
            database.close();
            return response;
        }
        catch(Exception e){
            LOGGER.error("Failed to get EventPermission: {} ", e.getMessage());
            database.close();
            return Optional.empty();
        }
    }

    // Get EventPermission accepts either EventUUID or UserUUID (as either *should* be unique)
    public Optional<List<EventPermission>> get(String uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s' OR HasUsers.UserUUID = '%s'", uuid, uuid);

            ResultSet rs = statement.executeQuery(query);
            List<EventPermission> records = new ArrayList<>();
            while(rs.next()){
                EventPermission record = new EventPermission(rs.getString(1), rs.getString(2), rs.getBoolean(3));
                records.add(record);
            }

            database.close();
            return Optional.ofNullable(records);

        }
        catch(Exception e){
            LOGGER.error("Failed to get EventPermission: {} ", e.getMessage());
            database.close();
            return Optional.empty();
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
        database.open();
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO HasUsers (EventUUID, UserUUID, IsOwner) VALUES (?,?,?)")){
            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner());
            int rows = statement.executeUpdate();

            return (rows > 0)? true : false;
            
        }
        catch(Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            database.close();
            return false;
        }
    }


    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventPermission t) {
        database.open();
        try(PreparedStatement statement = database.getCon().prepareStatement("UPDATE HasUsers SET EventUUID = ?, UserUUID = ?, IsOwner = ? WHERE EventUUID = ? AND UserUUID = ?")){

            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner());
            statement.setString(4, t.getEvent().toString());
            statement.setString(5, t.getUser().toString());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }
            else
                throw new SQLException("UPDATE on HasUsers failed");

        }catch(Exception e){
            LOGGER.error("Unable to update: {}", e.getMessage());
            database.close();
            return false;
        }
    }


    // Delete
    //-----------------------------------------------------------------


    @Override
    public Boolean delete(EventPermission t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(String EventUUID, String UserUUID) {
        database.open();

        try(Statement statement = database.con.createStatement()){
            String query = String.format("DELETE FROM HasUsers WHERE HasUsers.EventUUID = '%s' AND HasUsers.UserUUID = '%s'",
                    EventUUID, UserUUID);
            int rows = statement.executeUpdate(query);
            database.close();

            return (rows > 0)? true : false;

        }catch(Exception e){
            LOGGER.error("Failed to delete EventPermission: {} ", e.getMessage());
            database.close();
            return false;
        }
    }
    
}
