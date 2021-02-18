//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on EventResponse objects
package com.LetsMeet.LetsMeet.Event.DAO;

//-----------------------------------------------------------------

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;

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
    private static final Logger LOGGER=LoggerFactory.getLogger(EventPermissionDao.class);

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

    public Optional<EventResponse> get(UUID anyUUID){
        database.open();
        try(Statement statement = database.getCon().createStatement()){

            String query = String.format("select * from EventResponse where EventResponse.EventUUID = '%s' OR EventResponse.UserUUID = '%s'", anyUUID, anyUUID);

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

    @Override
    public Optional<Collection<EventResponse>> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(EventResponse t) {
        // TODO Auto-generated method stub
        return null;
    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventResponse t) {
        // TODO Auto-generated method stub
        return null;
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(EventResponse t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean delete(String uuid1, String uuid2) {
        // TODO Auto-generated method stub
        return null;
    }


    
}
