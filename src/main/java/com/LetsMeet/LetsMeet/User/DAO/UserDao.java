//-----------------------------------------------------------------
// UserDAOjava
// Let's Meet 2021
//
// Responsible for perfoming CRUD operations on User objects/records

package com.LetsMeet.LetsMeet.User.DAO;

//-----------------------------------------------------------------

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import com.LetsMeet.Models.Data.TokenData; //TODO This needs to be refactored can't use this import from external package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;

//-----------------------------------------------------------------

@Component
public class UserDao implements DAO<User> {

    // Components
    //-----------------------------------------------------------------

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DBConnector database;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<User> get(UUID uuid) {
        database.open();
        try (Statement statement = database.getCon().createStatement();) {
            String query = String.format("select * from User where User.UserUUID = '%s'", uuid);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            Optional<User> user = Optional.of( new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
            rs.getString(4), rs.getString(5), rs.getString(6)));
            database.close();
            return user;

        } catch (Exception e) {
            if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                System.out.println("\nUser DAO: get (UUID)");
                System.out.println(e);

                return Optional.empty();
            }         
        }
        return Optional.empty();
    }

    public Optional<User> get(String email){
        database.open();
        try (Statement statement = database.getCon().createStatement();) {
            String query = String.format("select * from User where User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            User user = new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6));

            database.close();
            return Optional.ofNullable(user);

        } catch (Exception e) {
            if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                System.out.println("\nUser DAO: get (String)");
                System.out.println(e);
            }
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<User>> getAll() {
        database.open();
        try(Statement statement = database.getCon().createStatement();){
            
            ResultSet rs = statement.executeQuery("select * from User");

            

            List<User> users = new ArrayList<>();

            while(rs.next()){
                users.add(new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                
            }
            database.close();
            return Optional.of(users);

        }catch(Exception e){
            System.out.println("\nUser DAO: allUsers");
            System.out.println(e);
            return Optional.empty();
        }
    }

    // Create
    //-----------------------------------------------------------------
    @Override
    public Boolean save(User t)  {
        database.open();

        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO User (UserUUID, fName, lName, email, PasswordHash, salt) VALUES (?,?,?,?,?,?)")) {
            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getfName());
            statement.setString(3, t.getlName());
            statement.setString(4, t.getEmail());
            statement.setString(5, t.getPasswordHash());
            statement.setString(6, t.getSalt());
            int rows = statement.executeUpdate();

            database.close();

            if (rows > 0) {
                return true;
            } else {
                throw new Exception("Nothing added to DB");
            }

        } catch (Exception e) {
            System.out.println("\nUser DAO: newUser");
            System.out.println(e);
            return false;
        }
    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(User t) {
        database.open();
        try(Statement statement = database.getCon().createStatement();){

            String query = String.format("UPDATE User SET fName = '%s', lName = '%s', email = '%s', PasswordHash = '%s', salt = '%s' WHERE UserUUID = '%s'", t.getfName(), t.getlName(), t.getEmail(), t.getPasswordHash(), t.getSalt(), t.getUUID().toString());
            statement.executeUpdate(query);

            database.close();

            return true;

        } catch(Exception e) {

            System.out.println("\nUser DAO: updateuser");
            System.out.println(e);
            database.close();
            return false;
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(User t) {
        database.open();
        try(Statement statement = database.getCon().createStatement();) {
            String query = String.format("DELETE FROM User WHERE User.UserUUID = '%s'", t.getUUID().toString());
            statement.executeUpdate(query);
            database.close();
            return true;
        } catch (Exception e) {
            System.out.println("\nUser DAO: deleteUser");
            System.out.println(e);
            return false;
        }
    }

    @Override
    public Boolean delete(UUID uuid) {
        // TODO Auto-generated method stub
        return false;

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean updatePassword(User user, String passwordHash){
        database.open();
        try(Statement statement = database.getCon().createStatement();){

            String query = String.format("UPDATE User SET PasswordHash = '%s' WHERE UserUUID = '%s'", passwordHash,
                    user.getUUID().toString());
            statement.executeUpdate(query);

            database.close();

            return true;

        } catch(Exception e) {

            System.out.println("\nUser DAO: updatePassword");
            System.out.println(e);
            database.close();
            return false;
        }
    }

    public Integer isAdmin(User user) {

        database.open();

        try(Statement statement = database.getCon().createStatement();){

            String query = String.format("SELECT isAdmin FROM User WHERE UserUUID = '%s'", user.getUUID().toString());

            ResultSet rs = statement.executeQuery(query);

            int value = 0;

            while (rs.next()) {

                value = rs.getInt(1);

            }

            database.close();

            return value;

        } catch(Exception e) {

            System.out.println("\nUser DAO: isAdmin");
            System.out.println(e);
            database.close();
            return null;

        }

    }

}