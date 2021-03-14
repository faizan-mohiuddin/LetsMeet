package com.LetsMeet.LetsMeet.Event.DAO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.EventResult;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;

import org.springframework.stereotype.Component;

@Component
public class EventResultDao implements DAO<EventResult> {

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<EventResult> get(UUID uuid) throws IOException {
        try(Statement statement = DatabaseInterface.get().createStatement()) {
            String query = String.format("select * from EventResult where EventResult.EventUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<EventResult> eventResponse = Optional.ofNullable(readSerialised(EventResult.class,rs.getBytes("EventResult")));

            DatabaseInterface.drop();
            return eventResponse;

        }catch(SQLException e){
            DatabaseInterface.drop();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<EventResult>> getAll() throws IOException {
        throw new IOException("Not implemented");
    }

    @Override
    public Boolean save(EventResult t) throws IOException {
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("INSERT INTO EventResult (EventUUID, EventResult) VALUES (?,?)")){

            statement.setString(1, t.getEventUUID().toString());
            statement.setObject(2, t);

            if(statement.executeUpdate() > 0){return true;}
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean update(EventResult t) throws IOException {
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("UPDATE EventResult SET EventResult = ? WHERE EventUUID = ?")){

            statement.setObject(1, t);
            statement.setString(2, t.getEventUUID().toString());
            
            if(statement.executeUpdate() > 0) return true;      
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean delete(EventResult t) throws IOException {
        throw new IOException("Not implemented");
    }

    @Override
    public Boolean delete(UUID uuid) throws IOException {
        throw new IOException("Not implemented");
    }

    private <T> T readSerialised(Class<T> readClass, byte[] buf){
        try{
            ObjectInputStream objectIn = null;
            // If bytes are present, try to deserialize
            if (buf != null){

                objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                Object object = objectIn.readObject();

                // Attempt to cast object to ConditionSet - is there a better way to do this?
                if (readClass.isAssignableFrom(object.getClass())){
                    return readClass.cast(object);  
                }
                
            }
            return null;
        }
        catch(Exception e){
            return null;
        }
    }
}
