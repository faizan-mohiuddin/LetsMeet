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
import java.sql.PreparedStatement;

import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventResponseDao implements DAOconjugate<EventResponse> {

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventResponse.class);

    @Autowired
    DBConnector database;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<EventResponse> get(UUID eventUUID, UUID userUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from EventResponse where EventResponse.EventUUID = '%s' and EventResponse.UserUUID = '%s'", eventUUID.toString(),userUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<EventResponse> response = Optional.ofNullable(new EventResponse(
                UUID.fromString(rs.getString("UserUUID")), 
                UUID.fromString(rs.getString("EventUUID")), 
                UUID.fromString(rs.getString("ConditionSetUUID"))));

            database.close();
            return response;

        }
        catch(Exception e){
            LOGGER.error("Unable to fetch from database: {} ", e.getMessage());
            database.close();
            return Optional.empty();
        }

    }

    public Optional<List<EventResponse>> get(UUID anyUUID){
        database.open();
        try(Statement statement = database.getCon().createStatement()){

            String query = String.format("select * from EventResponse where EventResponse.EventUUID = '%s' OR EventResponse.UserUUID = '%s'", anyUUID, anyUUID);

            ResultSet rs = statement.executeQuery(query);
            List<EventResponse> records = new ArrayList<>();

            while(rs.next())
                records.add(new EventResponse(
                    UUID.fromString(rs.getString("UserUUID")), 
                    UUID.fromString(rs.getString("EventUUID")), 
                    UUID.fromString(rs.getString("ConditionSetUUID"))));

            database.close();
            return Optional.ofNullable(records);

        }
        catch(Exception e){
            LOGGER.error("Unable to fetch from database: {} ", e.getMessage());
            database.close();
            return Optional.empty();
        }

    }

    @Override
    public Optional<Collection<EventResponse>> getAll() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(EventResponse t) {
        database.open();
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO EventResponse (EventUUID, UserUUID, ConditionSetUUID, PollResponseUUID) VALUES (?,?,?,?)")){
            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setString(3, t.getConditionSet().toString());
            statement.setString(4, "{}");
            int rows = statement.executeUpdate();

            database.close();

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
    public Boolean update(EventResponse t) {
        database.open();
        try(PreparedStatement statement = database.getCon().prepareStatement("UPDATE EventResponse SET EventUUID = ?, UserUUID = ?, ConditionSetUUID = ? WHERE EventUUID = ? AND UserUUID = ?")){

            statement.setString(1, t.getEvent().toString());
            statement.setString(2, t.getUser().toString());
            statement.setString(3, t.getConditionSet().toString());
            statement.setString(4, t.getEvent().toString());
            statement.setString(5, t.getUser().toString());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }
            else
                throw new SQLException("UPDATE on EventResponse failed");

        }catch(Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            database.close();
            return false;
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(EventResponse t) {
        database.open();
        try(Statement statement = database.con.createStatement()){
            String query = String.format("DELETE FROM EventResponse WHERE EventResponse.EventUUID = '%s' AND EventResponse.UserUUID = '%s'",
                    t.getEvent().toString(),t.getUser().toString());
            int rows = statement.executeUpdate(query);
            database.close();

            if(rows <= 0)
                throw new SQLException("UPDATE on EventResponse failed");
            return true;

        }catch(Exception e){
            LOGGER.error("Unable to delete: {}", e.getMessage());
            database.close();
            return false;
        }
    }

    @Override
    public Boolean delete(String eventUUID, String userUUID) {
        return delete(get(UUID.fromString(eventUUID), UUID.fromString(userUUID)).get());
    }


    
}
