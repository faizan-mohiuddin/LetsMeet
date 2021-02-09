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

import java.util.*;

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

            Optional<EventPermission> response = Optional.ofNullable(new EventPermission(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)), rs.getBoolean(3)));
            database.close();
            return response;

        }
        catch(Exception e){
            System.out.println("\nEvent Permission Dao: get(UUID, UUID");
            //e.printStackTrace();
            System.out.println(e);
            database.close();
            return Optional.empty();
        }
    }

    public Optional<List<EventPermission>> get(String event) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasUsers where HasUsers.EventUUID = '%s'", event);

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
            e.printStackTrace();
            database.close();
            return Optional.empty();
        }
    }

    public Optional<List<EventPermission>> getByUser(String userUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasUsers where HasUsers.UserUUID = '%s'", userUUID);

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
            statement.setString(2, t.getUser().toString());
            statement.setBoolean(3, t.getIsOwner());
            int rows = statement.executeUpdate();

            if(rows > 0){
                return true;
            }else{
                return false;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
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
    public Boolean delete(String EventUUID, String UserUUID) {
        database.open();

        try(Statement statement = database.con.createStatement()){
            String query = String.format("DELETE FROM HasUsers WHERE HasUsers.EventUUID = '%s' AND HasUsers.UserUUID = '%s'",
                    EventUUID, UserUUID);
            int rows = statement.executeUpdate(query);

            if(rows <= 0){
                database.close();
                return false;
            }
            database.close();
            return true;

        }catch(Exception e){
            database.close();
            System.out.println("\nEvent Permission Dao : delete (String, String)");
            System.out.println(e);
            return false;
        }
    }
    
}
