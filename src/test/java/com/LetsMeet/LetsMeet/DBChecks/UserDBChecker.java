package com.LetsMeet.LetsMeet.DBChecks;

import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

@Component
public class UserDBChecker {

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DBConnector database;

    @Autowired
    UserService userService;

    @Autowired
    VenueService venueService;

    public void removeUserByEmail(String email){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("DELETE FROM User WHERE User.email = '%s'", email);
            statement.executeUpdate(query);
            database.close();
        }catch(Exception e){
            database.close();
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
        }
    }

    public boolean checkForToken(String token, String UserUUID){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT Token.UserUUID, Token.TokenUUID FROM Token " +
                    "WHERE Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            if(rs.getString("UserUUID").equals(UserUUID) && rs.getString("TokenUUID").equals(token)) {
                database.close();
                return true;
            }
            database.close();
            return false;
        }catch(Exception e){
            database.close();
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
            return false;
        }
    }

    public String UserUUIDFromEmail(String email){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT User.UserUUID FROM User WHERE User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);
            rs.next();

            String response = rs.getString("UserUUID");
            database.close();
            return response;
        }catch(Exception e){
            database.close();
            System.out.println("UserDBChecker : UserUUIDFromEmail");
            System.out.println(e);
            return null;
        }
    }

    public void clearTestData(){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("Select * FROM User WHERE User.email like '%%InternalTesting.com'");
            ResultSet rs = statement.executeQuery(query);

            String userUUID;
            User user;

            while(rs.next()){
                userUUID = rs.getString(1);
                user = new User(userUUID);
                userService.deleteUser(user);
            }
            database.close();
        }catch(Exception e){
            database.close();
            System.out.println("\nUserDBChecker : clearTestUsers");
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }
}
