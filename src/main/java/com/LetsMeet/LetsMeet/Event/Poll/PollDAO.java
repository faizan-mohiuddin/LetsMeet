package com.LetsMeet.LetsMeet.Event.Poll;

import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PollDAO implements DAO<Poll>{

    @Autowired
    ConnectionService connectionService;

    private static final Gson gson = new Gson();

    @Override
    public Optional<Poll> get(UUID uuid) throws IOException {  
        try(DatabaseConnector connector = connectionService.get();
        Statement statement = connector.getConnection().createStatement()) {
        String query = String.format("select * from Event_Poll where Event_Poll.UUID = '%s'", uuid);

        ResultSet rs = statement.executeQuery(query);
        
        if (!rs.next())
            return Optional.empty();

        return Optional.of(new Poll(
            UUID.fromString(rs.getString(1)), 
            rs.getString(2), 
            new Gson().fromJson(rs.getString(4), new TypeToken<Map<String, Integer>>() {}.getType()),
            rs.getBoolean(3)));
  
        }
        catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<Poll>> getAll() throws IOException {
        return Optional.empty();
    }

    @Override
    public Boolean save(Poll t) throws IOException {
        
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO Event_Poll (UUID, Name, Multiselect, Options) VALUES (?,?,?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getName());
            statement.setBoolean(3, t.getSelectMultiple());
            statement.setString(4, gson.toJson(t.getOptions()));
            

            if(statement.executeUpdate() > 0){return true;}
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean update(Poll t) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("UPDATE Event_Poll SET Name = ?, Multiselect = ?, Options = ? WHERE UUID = ?")){

            statement.setString(4, t.getUUID().toString());
            statement.setString(1, t.getName());
            statement.setBoolean(2, t.getSelectMultiple());
            statement.setString(3, gson.toJson(t.getOptions()));

            if(statement.executeUpdate() > 0) return true;      
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean delete(Poll t) throws IOException {
        return delete(t.getUUID());
    }

    @Override
    public Boolean delete(UUID uuid) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        Statement statement = connector.getConnection().createStatement()){
    
        String query;

        query = String.format("DELETE FROM Event_Poll where UUID = '%s'", uuid.toString());
        statement.executeUpdate(query);

        return true;

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }
    
}
