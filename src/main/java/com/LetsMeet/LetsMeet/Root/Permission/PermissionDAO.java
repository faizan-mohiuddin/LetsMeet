package com.LetsMeet.LetsMeet.Root.Permission;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.Root.Permission.Model.Permission;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionDAO implements DAOconjugate<Permission> {

    private static final Logger LOGGER=LoggerFactory.getLogger(PermissionDAO.class);

    @Autowired
    ConnectionService connectionService;

    @Override
    public Optional<Permission> get(UUID parent, UUID child) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("select * from Permission where Permission.parent = '%s' and Permission.child = '%s'", parent.toString(),child.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            return Optional.ofNullable(new Permission(
                UUID.fromString(rs.getString(1)),
                UUID.fromString(rs.getString(2)),
                Permission.Type.valueOfCode(rs.getString(3))
            ));

        }catch(SQLException e){
            
            throw new IOException(e.getMessage());
        }
    }

    public Optional<List<Permission>> get(UUID any)throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        Statement statement = connector.getConnection().createStatement();){
            String query = String.format("select * from Permission where Permission.parent = '%s' OR Permission.child = '%s'", any, any);

            ResultSet rs = statement.executeQuery(query);
            List<Permission> records = new ArrayList<>();
            while(rs.next()){
                records.add( new Permission(
                    UUID.fromString(rs.getString(1)),
                    UUID.fromString(rs.getString(2)),
                    Permission.Type.valueOfCode(rs.getString(3))
                ));
            }
            return Optional.ofNullable(records);
        }
        catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }
    

    @Override
    public Optional<Collection<Permission>> getAll() throws IOException {
        return Optional.empty();
    }

    @Override
    public Boolean save(Permission t) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO Permission (parent, child, code) VALUES (?,?,?)")){
            statement.setString(1, t.getParent().toString());
            statement.setString(2, t.getChild().toString());
            statement.setString(3, t.getType().code);
            int rows = statement.executeUpdate();

            return (rows > 0)? true : false;
            
        }
        catch(Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean update(Permission t) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
        PreparedStatement statement = connector.getConnection().prepareStatement("UPDATE Permission SET code = ? WHERE parent = ? AND child = ?")){
            statement.setString(2, t.getParent().toString());
            statement.setString(3, t.getChild().toString());
            statement.setString(1, t.getType().code);

            return (statement.executeUpdate() > 0);
            
        }
        catch(Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean delete(Permission t) throws IOException {
        return delete(t.getParent().toString(), t.getChild().toString());
    }

    @Override
    public Boolean delete(String parent, String child) throws IOException {
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){

            String query = String.format("DELETE FROM Permission WHERE Permission.parent = '%s' AND Permission.child = '%s'", parent, child);
  
            return (statement.executeUpdate(query) > 0);

        }catch(Exception e){
            throw new IOException("Failed to delete Permission:" + e.getMessage());
        }
    }
    
}
