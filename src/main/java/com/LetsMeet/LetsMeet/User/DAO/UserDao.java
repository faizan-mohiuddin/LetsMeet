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

import com.LetsMeet.Models.TokenData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            String query = String.format("select * from User where User.UserrUUID = '%s'", uuid);
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            Optional<User> user = Optional.of( new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
            rs.getString(4), rs.getString(5), rs.getString(6)));
            database.close();
            return user;

        } catch (Exception e) {
            if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                System.out.println("\nUser DAO: getUserByEmail");
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
                System.out.println("\nUser DAO: getUserByEmail");
                System.out.println(e);
            }
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getAll() {
        database.open();
        try(Statement statement = database.getCon().createStatement();){
            
            ResultSet rs = statement.executeQuery("select * from User");

            

            List<User> users = new ArrayList<>();

            while(rs.next()){
                users.add(new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                
            }
            database.close();
            return users;

        }catch(Exception e){
            System.out.println("\nUser DAO: allUsers");
            System.out.println(e);
            return null;
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
        // TODO Auto-generated method stub
        return false;
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

    //TODO methods below must have references to them refactored and then be depreciated
    //Note that DAO is for CRUD ops only, any business logic is handled by the service layer. 
    //Note that Tokens now have their own DAO
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkEmailExists(String email){
        database.open();

        // Returns true if the email exists
        try (Statement statement = database.getCon().createStatement();){
            String query = String.format("SELECT COUNT(email) AS Emails FROM User WHERE User.email = '%s'", email);

            ResultSet rs = statement.executeQuery(query);

            rs.next();

            int count = rs.getInt(1);
            database.close();

            if (count > 0) {
                return true;
            }
            return false;

        } catch (Exception e) {
            System.out.println("User DAO: checkEmailExists");
            System.out.println(e);
            return true;
            }
    }



    public boolean CheckUserToken(String UUID){
        database.open();
            try (Statement statement = database.getCon().createStatement();) {
                String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.UserUUID = '%s'", UUID);
                ResultSet rs = statement.executeQuery(query);

                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    database.close();
                    return true;
                } else {
                    database.close();
                    return false;
                }

            } catch (Exception e) {
                System.out.println("\nUser DAO: CheckUserToken");
                System.out.println(e);
                return false;
            }
    }

    public void removeAllUserToken(String UUID){
        // Remove all tokens corresponding to a user
        try(Statement statement = database.getCon().createStatement();){
            database.open();
            String query = String.format("DELETE FROM Token WHERE Token.UserUUID = '%s'", UUID);
            statement.executeUpdate(query);
            database.close();
        }catch(Exception e){
            System.out.println("\nUser DAO: removeALLUserTokens");
            System.out.println(e);
        }
    }

    public String createToken(String UUID, String Token, long expires){
        database.open();
        try (PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");){

            statement.setString(1, UUID);
            statement.setString(2, Token);
            statement.setLong(3, expires);
            int rows = statement.executeUpdate();

            database.close();

            if (rows > 0) {
                return "Token created successfully";
            } else {
                throw new Exception("Error creating token");
            }

        } catch (Exception e) {
            System.out.println("\nUser DAO: createToken");
            System.out.println(e);
            return "Error creating API token";
        }
    }

    public TokenData getTokenRecord(String token){
        database.open();
            try (Statement statement = database.getCon().createStatement();){
                String query = String.format("select * from Token where Token.TokenUUID = '%s'", token);
                ResultSet rs = statement.executeQuery(query);
                if(rs.next()){
                    TokenData data = new TokenData();
                    data.populate(rs.getString(1), rs.getString(2), rs.getInt(3));
                    database.close();
                    return data;
                }else{
                    // Incorrect token
                    return null;
                }

            } catch (Exception e) {
                System.out.println("\nUser DAO: getTokenRecord");
                System.out.println(e);
                return null;
            }
    }

    public String getUserUUIDByToken(String token){
        database.open();
        try (Statement statement = database.getCon().createStatement();) {
            String query = String.format("select UserUUID from Token where Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String r = rs.getString(1);
            database.close();
            return r;

        } catch (Exception e) {
            System.out.println("\nUser DAO: getUserByToken");
            System.out.println(e);
            return null;
        }
    }

    public User getUserByUUID(String uuid){
        database.open();
            try (Statement statement = database.getCon().createStatement();) {
                String query = String.format("select * from User where User.UserUUID = '%s'", uuid);

                ResultSet rs = statement.executeQuery(query);
                rs.next();

                User user = new User(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6));

                database.close();
                return user;

            } catch (Exception e) {
                System.out.println("\nUser DAO: getUserByUUID");
                System.out.println(e);
                return null;
            }
    }


}
