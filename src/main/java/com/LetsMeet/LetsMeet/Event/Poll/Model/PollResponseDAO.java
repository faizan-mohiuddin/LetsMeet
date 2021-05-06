package com.LetsMeet.LetsMeet.Event.Poll.Model;

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
public class PollResponseDAO implements DAO<PollResponse>{

    @Autowired
    ConnectionService connectionService;

    private static Gson gson = new Gson();

    @Override
    public Optional<PollResponse> get(UUID uuid) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        Statement statement = connector.getConnection().createStatement();) {
        String query = String.format("select * from Event_Poll_Response where Event_Poll_Response.UUID = '%s'", uuid);

        ResultSet rs = statement.executeQuery(query);
        
        if (!rs.next())
            return Optional.empty();

        return Optional.ofNullable(new PollResponse(
            UUID.fromString(rs.getString(1)),
            gson.fromJson(rs.getString(2), new TypeToken<Map<String, Boolean>>() {}.getType())
            ));
            
        }
        catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<PollResponse>> getAll() throws IOException {
        return Optional.empty();
    }

    @Override
    public Boolean save(PollResponse t) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO Event_Poll_Response (UUID, Selection) VALUES (?,?)")){

            statement.setString(1, t.getUUID().toString());
            statement.setString(2, gson.toJson(t.getResponses()));

            if(statement.executeUpdate() > 0) return true;      
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean update(PollResponse t) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("UPDATE Event_Poll_Response SET Selection = ? WHERE UUID = ?")){

            statement.setString(2, t.getUUID().toString());
            statement.setString(1, gson.toJson(t.getResponses()));

            if(statement.executeUpdate() > 0) return true;      
            else throw new IOException("No data written" + statement.getWarnings().getErrorCode());

        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Boolean delete(PollResponse t) throws IOException {
        return delete(t.getUUID());
    }

    @Override
    public Boolean delete(UUID uuid) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        Statement statement = connector.getConnection().createStatement();){
    
        String query;

        query = String.format("DELETE FROM Event_Poll_Response where UUID = '%s'", uuid.toString());
        statement.executeUpdate(query);

        return true;

        }catch(SQLException e){
            throw new IOException(e.getMessage());
        }
    }   
}
