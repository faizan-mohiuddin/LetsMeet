//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for perfoming CRUD operations on EventPermission objects/records

package com.LetsMeet.LetsMeet.Event.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//-----------------------------------------------------------------

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class EventPermissionDao implements DAOconjugate<EventPermission> {

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
            database.close();

            return Optional.ofNullable(new EventPermission(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)), rs.getBoolean(3)));

        }
        catch(Exception e){
            e.printStackTrace();
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
            statement.setString(1, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner().booleanValue());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventPermission t) {
        // TODO Auto-generated method stub
        return false;
    }


    // Delete
    //-----------------------------------------------------------------


    @Override
    public Boolean delete(EventPermission t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(UUID event, UUID user) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
