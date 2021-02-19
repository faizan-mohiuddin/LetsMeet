package com.LetsMeet.LetsMeet.Event.DAO;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConditionSetDao implements DAO<ConditionSet> {

    // Components
    //-----------------------------------------------------------------

    private static final Logger LOGGER=LoggerFactory.getLogger(ConditionSetDao.class);

    @Autowired
    DBConnector database;


    @Override
    public Optional<ConditionSet> get(UUID uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from ConditionSet where ConditionSet.ConditionSetUUID = '%s'", uuid.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            /* Deserialize ConditionSet from byte stream */
            // Get bytes
            byte[] buf = rs.getBytes(3);
            ObjectInputStream objectIn = null;
            database.close();

            // If bytes are present, try to deserialize
            if (buf != null){

                objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                Object object = objectIn.readObject();

                // Attempt to cast object to ConditionSet - is there a better way to do this?
                if (object instanceof ConditionSet){
                    return Optional.ofNullable((ConditionSet)object);
                }

                else
                    LOGGER.error("Unable to read ConditionSet. Expected ConditionSet, received {}", object.getClass().getName());
            }

            else
                LOGGER.error("Unable to read ConditionSet. Input stream empty ");
            return Optional.empty();
            
            

        }catch(Exception e){
            database.close();
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<ConditionSet>> getAll() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    // Save
    //-----------------------------------------------------------------

    @Override
    public Boolean save(ConditionSet t) {
        database.open();
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO ConditionSet (ConditionSetUUID, Name, Object) VALUES (?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setObject(3, t);

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            database.close();
            LOGGER.error(e.getMessage());
            return false;
        }

    }

    @Override
    public Boolean update(ConditionSet t) {
        database.open();
        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("UPDATE ConditionSet SET Name = ?, Object = ? WHERE ConditionSetUUID = ?")){

            statement.setString(1, t.getName());
            statement.setObject(2, t);
            statement.setString(3, t.getUUID().toString());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            database.close();
            LOGGER.error("Could Not Update: {}", e.getMessage());
            return false;
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(ConditionSet t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(UUID uuid) {
        database.open();
        try(Statement statement = database.con.createStatement()){
        
            String query;
            String eventUUID = uuid.toString();

            query = String.format("DELETE FROM ConditionSet where ConditionSet.ConditionSetUUID = '%s'", eventUUID);
            statement.executeUpdate(query);

            return true;

        }catch(Exception e){
            database.close();
            LOGGER.error("Could Not Delete: {}", e.getMessage());
            return false;
        }
    }

}