package com.LetsMeet.LetsMeet.User.DAO;

import java.sql.ResultSet;
import java.sql.*;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.User.Model.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/*

@Component
public class TokenDAO implements DAO<Token> {

    @Autowired
    DBConnector database;

    @Override
    public Optional<Token> get(UUID uuid) {
        database.open();
        try (Statement statement = database.con.createStatement();) {
            String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.UserUUID = '%s'", uuid);
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
            System.out.println("\nUser DAO: CheckUserToken");
            System.out.println(e);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Token> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int save(Token t) {
        database.open();
        try (PreparedStatement statement = database.con.prepareStatement("INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");){

            statement.setString(1, t.getUserUUID().toString());
            statement.setString(2, t.getUUID().toString());
            statement.setLong(3, t.getExpires());
            int rows = statement.executeUpdate();

            database.close();

            if (rows > 0) {
                return 1;
            } else {
                throw new Exception("Error creating token");
            }

        } catch (Exception e) {
            System.out.println("\nUser DAO: createToken");
            System.out.println(e);
            return 0;
        }
    }

    @Override
    public void update(Token t) {
        // TODO Auto-generated method stub

    }

    @Override
    public String delete(Token t) {
        
        try(Statement statement = database.con.createStatement();) {
            database.open();
            String query = String.format("DELETE FROM User WHERE Token.TokenUUID = '%s'", t.getUUID().toString());
            statement.executeUpdate(query);
            database.close();
            return "Token successfully deleted.";
        } catch (Exception e) {
            System.out.println("\nUser DAO: deleteUser");
            System.out.println(e);
            return "Error deleting user";
        }
    }

    @Override
    public void delete(UUID uuid) {
        this.delete(this.get(uuid).get());
    }
    
}
*/