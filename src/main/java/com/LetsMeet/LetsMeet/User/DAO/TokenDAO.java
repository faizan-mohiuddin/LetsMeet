//-----------------------------------------------------------------
// TokenDAO.java
// Let's Meet 2021
//
// Responsible for perfoming CRUD operations on Token objects/records

package com.LetsMeet.LetsMeet.User.DAO;

//-----------------------------------------------------------------
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.User.Model.Token;
import com.LetsMeet.LetsMeet.User.Model.User;

import com.LetsMeet.Models.Data.TokenData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class TokenDAO implements DAO<Token> {

    // Components
    //-----------------------------------------------------------------

    @Autowired
    ConnectionService connectionService;

    // Get
    //-----------------------------------------------------------------
    @Override
    public Optional<Token> get(UUID uuid) {
        
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("SELECT * FROM Token WHERE Token.TokenUUID = '%s'", uuid);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            int count = rs.getInt(1);
            Optional<Token> token = Optional.ofNullable(
                new Token(rs.getString(2), UUID.fromString(rs.getString(1)), rs.getInt(3)));

            if (count > 0) {
                
                return token;
            } else {
                
                return Optional.empty();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<Token>> getAll() {
        return Optional.empty();
    }

    // Returns all Token objects belonging to a user
    public Optional<Collection<Token>> getAll(User user){
        
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            // Create query
            String query = String.format("SELECT * FROM Token WHERE Token.UserUUID = '%s'", user.getUUID().toString());

            // Run query
            ResultSet rs = statement.executeQuery(query);

            // Store results
            ArrayList<Token> tokens = new ArrayList<>();

            while(rs.next()){
                tokens.add(new Token(rs.getString(2), UUID.fromString(rs.getString(1)), rs.getInt(3)));
            }

            Optional<Collection<Token>> response = Optional.ofNullable(tokens);
            
            return response;
        } 
        catch (Exception e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }

    // Create
    //-----------------------------------------------------------------

    @Override
    public Boolean save(Token t) {
        
        try (DatabaseConnector connector = connectionService.get();
            PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");){

            statement.setString(1, t.getUserUUID().toString());
            statement.setString(2, t.getToken());
            statement.setLong(3, t.getExpires());
            int rows = statement.executeUpdate();

            

            if (rows > 0) {
                return true;
            } else {
                throw new SQLException("Error creating token");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update
    //-----------------------------------------------------------------
    @Override
    public Boolean update(Token t) {
        // TODO Auto-generated method stub
        return false;

    }

    // Delete
    //-----------------------------------------------------------------

    // Delete from a Token Object
    @Override
    public Boolean delete(Token t) {
        

        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("DELETE FROM Token WHERE Token.TokenUUID = '%s'", t.getToken());
            statement.executeUpdate(query);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete any with matching UUID
    @Override
    public Boolean delete(UUID uuid) {
        return this.delete(this.get(uuid).get());
    }

    // Other methods
    // In use
    public TokenData getTokenRecord(String token){
        
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            String query = String.format("select * from Token where Token.TokenUUID = '%s'", token);
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                TokenData data = new TokenData();
                data.populate(rs.getString(1), rs.getString(2), rs.getInt(3));
                
                return data;
            }else{
                // Incorrect token
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // In use
    public String getUserUUIDByToken(String token){
        
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("select UserUUID from Token where Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String r = rs.getString(1);
            
            return r;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
