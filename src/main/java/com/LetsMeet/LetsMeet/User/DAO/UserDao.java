package com.LetsMeet.LetsMeet.User.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Model.User_Internal;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import com.LetsMeet.Models.AdminUserData;
import com.LetsMeet.Models.TokenData;
import com.LetsMeet.Models.UserData;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends DBConnector implements DAO<User_Internal> {

    @Autowired
    LetsMeetConfiguration config;

    //Connection con;

    public UserDao(){
        super();
        //this.con = super.con;
    }

    @Override
    public Optional<User_Internal> get(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<User> getAll() {
        // TODO Auto-generated method stub
        this.open();
        try{
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("select * from User");

            this.close();

            List<User> users = new ArrayList<>();

            while(rs.next()){
                User user = new User(rs.getString(2), rs.getString(3), rs.getString(4));
                users.add(user);
            }
            return users;

        }catch(Exception e){
            System.out.println("\nUser DAO: allUsers");
            System.out.println(e);
            return null;
        }
    }

    @Override
    public int save(User_Internal t)  {
        // TODO Auto-generated method stub
        this.open();

        try(PreparedStatement statement = this.con.prepareStatement("INSERT INTO User (UserUUID, fName, lName, email, PasswordHash, salt) VALUES (?,?,?,?,?,?)")) {
            statement.setString(1, t.getStringUUID());
            statement.setString(2, t.getfName());
            statement.setString(3, t.getlName());
            statement.setString(4, t.getEmail());
            statement.setString(5, t.getPasswordHash());
            statement.setString(6, t.getSalt());
            int rows = statement.executeUpdate();
            
            this.close();

            if (rows > 0) {
                return 1;
            } else {
                throw new Exception("Nothing added to DB");
            }

        } catch (Exception e) {
            System.out.println("\nUser DAO: newUser");
            System.out.println(e);
            return 0;
        }
    }

    @Override
    public void update(User_Internal t) {
        // TODO Auto-generated method stub

    }

    @Override
    public String delete(User_Internal t) {
        // TODO Auto-generated method stub
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("DELETE FROM User WHERE User.UserUUID = '%s'", t.getStringUUID());
                statement.executeUpdate(query);
                this.close();
                return "User successfully deleted.";
            } catch (Exception e) {
                System.out.println("\nUser DAO: deleteUser");
                System.out.println(e);
                return "Error deleting user";
            }
        }
        return "Error connecting";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkEmailExists(String email){
        this.open();
        if(this.checkCon()) {
            // Returns true if the email exists
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("SELECT COUNT(email) AS Emails FROM User WHERE User.email = '%s'", email);

                ResultSet rs = statement.executeQuery(query);

                rs.next();

                int count = rs.getInt(1);
                this.close();

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
        return false;
    }

    public User_Internal getUserByEmail(String email){
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("select * from User where User.email = '%s'", email);
                ResultSet rs = statement.executeQuery(query);

                rs.next();
                User_Internal user = new User_Internal(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6));

                this.close();
                return user;

            } catch (Exception e) {
                if(!e.getMessage().equals("Illegal operation on empty result set.")) {
                    System.out.println("\nUser DAO: getUserByEmail");
                    System.out.println(e);
                }
                return null;
            }
        }
        return null;
    }

    public boolean CheckUserToken(String UUID){
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.UserUUID = '%s'", UUID);
                ResultSet rs = statement.executeQuery(query);

                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    this.close();
                    return true;
                } else {
                    this.close();
                    return false;
                }

            } catch (Exception e) {
                System.out.println("\nUser DAO: CheckUserToken");
                System.out.println(e);
                return false;
            }
        }
        return false;
    }

    public void removeAllUserToken(String UUID){
        // Remove all tokens corresponding to a user
        try{
            this.open();
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM Token WHERE Token.UserUUID = '%s'", UUID);
            statement.executeUpdate(query);
            this.close();
        }catch(Exception e){
            System.out.println("\nUser DAO: removeALLUserTokens");
            System.out.println(e);
        }
    }

    public String createToken(String UUID, String Token, long expires){
        this.open();
        if(this.checkCon()) {
            try {
                PreparedStatement statement = this.con.prepareStatement(
                        "INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");
                statement.setString(1, UUID);
                statement.setString(2, Token);
                statement.setLong(3, expires);
                int rows = statement.executeUpdate();

                this.close();

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
        return "Error connecting";
    }

    public TokenData getTokenRecord(String token){
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("select * from Token where Token.TokenUUID = '%s'", token);

                ResultSet rs = statement.executeQuery(query);
                if(rs.next()){
                    TokenData data = new TokenData();
                    data.populate(rs.getString(1), rs.getString(2), rs.getInt(3));
                    this.close();
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
        return null;
    }

    public String getUserUUIDByToken(String token){
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("select UserUUID from Token where Token.TokenUUID = '%s'", token);

                ResultSet rs = statement.executeQuery(query);
                rs.next();
                String r = rs.getString(1);
                this.close();
                return r;

            } catch (Exception e) {
                System.out.println("\nUser DAO: getUserByToken");
                System.out.println(e);
                return null;
            }
        }
        return "Error connecting";
    }

    public User_Internal getUserByUUID(String uuid){
        this.open();
        if(this.checkCon()) {
            try {
                Statement statement = this.con.createStatement();
                String query = String.format("select * from User where User.UserUUID = '%s'", uuid);

                ResultSet rs = statement.executeQuery(query);
                rs.next();

                User_Internal user = new User_Internal(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6));

                this.close();
                return user;

            } catch (Exception e) {
                System.out.println("\nUser DAO: getUserByUUID");
                System.out.println(e);
                return null;
            }
        }
        return null;
    }
    
}
