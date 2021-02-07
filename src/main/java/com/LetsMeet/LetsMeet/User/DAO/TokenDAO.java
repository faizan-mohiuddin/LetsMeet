//-----------------------------------------------------------------
// TokenDAO.java
// Let's Meet 2021
//
// Responsible for perfoming CRUD operations on Token objects/records

package com.LetsMeet.LetsMeet.User.DAO;

//-----------------------------------------------------------------

import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.User.Model.Token;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class TokenDAO implements DAO<Token> {

    // Components
    //-----------------------------------------------------------------

    @Autowired
    DBConnector database;

    // Get
    //-----------------------------------------------------------------
    @Override
    public Optional<Token> get(UUID uuid) {
        database.open();
        try (Statement statement = database.getCon().createStatement();) {
            String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.TokenUUID = '%s'", uuid);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            int count = rs.getInt(1);
            Optional<Token> token = Optional.ofNullable(
                new Token(UUID.fromString(rs.getString(2)), UUID.fromString(rs.getString(1)), rs.getInt(3)));

            if (count > 0) {
                database.close();
                return token;
            } else {
                database.close();
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
        database.open();
        try (Statement statement = database.getCon().createStatement();) {
            // Create query
            String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.UserUUID = '%s'", user.getUUID().toString());

            // Run query
            ResultSet rs = statement.executeQuery(query);

            // Store results
            ArrayList<Token> tokens = new ArrayList<>();
            while(rs.next()){
                tokens.add(new Token(UUID.fromString(rs.getString(2)), UUID.fromString(rs.getString(1)), rs.getInt(3)));
            }

            database.close();
            return Optional.ofNullable(tokens);
        } 
        catch (Exception e) {
            e.printStackTrace();
            database.close();
            return Optional.empty();
        }
    }

    // Create
    //-----------------------------------------------------------------

    @Override
    public Boolean save(Token t) {
        database.open();
        try (PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");){

            statement.setString(1, t.getUserUUID().toString());
            statement.setString(2, t.getUUID().toString());
            statement.setLong(3, t.getExpires());
            int rows = statement.executeUpdate();

            database.close();

            if (rows > 0) {
                return true;
            } else {
                throw new Exception("Error creating token");
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
        
        try(Statement statement = database.getCon().createStatement();) {
            database.open();
            String query = String.format("DELETE FROM User WHERE Token.TokenUUID = '%s'", t.getUUID().toString());
            statement.executeUpdate(query);
            database.close();
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
    
}
