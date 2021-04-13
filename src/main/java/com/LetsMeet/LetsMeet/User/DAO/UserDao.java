//-----------------------------------------------------------------
// UserDAOjava
// Let's Meet 2021
//
// Responsible for performing CRUD operations on User objects/records

package com.LetsMeet.LetsMeet.User.DAO;

//-----------------------------------------------------------------
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//-----------------------------------------------------------------

@Component
public class UserDao implements DAO<User> {

    // Components
    //-----------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    ConnectionService connectionService;

    // Get
    //-----------------------------------------------------------------

    @Override
    public Optional<User> get(UUID uuid) {
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("select * from User where User.UserUUID = '%s'", uuid);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            Optional<User> user = Optional.of( new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
            rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7), rs.getBoolean(8)));
            return user;

        } catch (Exception e) {
            if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                return Optional.empty();
            }
            else{
                LOGGER.warn("Could not get user: {}", e.getMessage()); 
                return Optional.empty();
            }
        }
        
    }

    public Optional<User> get(String email){
        
        try (DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("select * from User where User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            User user = new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7));

            
            return Optional.ofNullable(user);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<User>> getAll() {
        
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){
            
            ResultSet rs = statement.executeQuery("select * from User");

            

            List<User> users = new ArrayList<>();

            while(rs.next()){
                users.add(new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getBoolean(7)));
                
            }
            
            return Optional.of(users);

        }catch(Exception e){
            return Optional.empty();
        }
    }

    // Create
    //-----------------------------------------------------------------
    @Override
    public Boolean save(User t)  {

        try(DatabaseConnector connector = connectionService.get();
            PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO User (UserUUID, " +
                    "fName, lName, email, PasswordHash, salt, isAdmin, isGuest) VALUES (?,?,?,?,?,?,?,?)")) {
            statement.setString(1, t.getUUID().toString());
            statement.setString(2, t.getfName());
            statement.setString(3, t.getlName());
            statement.setString(4, t.getEmail());
            statement.setString(5, t.getPasswordHash());
            statement.setString(6, t.getSalt());
            statement.setBoolean(7, t.getIsAdmin());
            statement.setBoolean(8, t.getIsGuest());
            int rows = statement.executeUpdate();

            if (rows > 0) {
                return true;
            } else {
                throw new SQLException("Nothing added to DB");
            }

        } catch (Exception e) {
            LOGGER.warn("Could not save user: {}", e.getMessage());
            return false;
        }
    }

    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(User t) {
        
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){

            String query = String.format("UPDATE User SET fName = '%s', lName = '%s', email = '%s', PasswordHash = '%s', salt = '%s' WHERE UserUUID = '%s'", t.getfName(), t.getlName(), t.getEmail(), t.getPasswordHash(), t.getSalt(), t.getUUID().toString());
            statement.executeUpdate(query);

            return true;

        } catch(Exception e) {
            LOGGER.warn("Could not update user: {}", e.getMessage());
            return false;
        }
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(User t) {
        
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();) {
            String query = String.format("DELETE FROM User WHERE User.UserUUID = '%s'", t.getUUID().toString());
            statement.executeUpdate(query);
            
            return true;
        } catch (Exception e) {
            LOGGER.warn("Could not delete user: {}", e.getMessage());
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
        
        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){

            String query = String.format("UPDATE User SET PasswordHash = '%s' WHERE UserUUID = '%s'", passwordHash,
                    user.getUUID().toString());
            statement.executeUpdate(query);

            

            return true;

        } catch(Exception e) {
            
            return false;
        }
    }

    public Integer isAdmin(User user) {

        

        try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){

            String query = String.format("SELECT isAdmin FROM User WHERE UserUUID = '%s'", user.getUUID().toString());

            ResultSet rs = statement.executeQuery(query);

            int value = 0;

            while (rs.next()) {

                value = rs.getInt(1);

            }

            

            return value;

        } catch(Exception e) {

            
            return null;

        }

    }

}