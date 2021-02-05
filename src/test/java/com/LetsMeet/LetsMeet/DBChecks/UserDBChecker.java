package com.LetsMeet.LetsMeet.DBChecks;

import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import java.sql.*;

public class UserDBChecker extends DBConnector{

    Connection con;

    public UserDBChecker(){
        super();
        this.con = super.con;
    }

    public void removeUserByEmail(String email){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM User WHERE User.email = '%s'", email);
            statement.executeUpdate(query);
        }catch(Exception e){
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
        }
    }

    public boolean checkForToken(String token, String UserUUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT Token.UserUUID, Token.TokenUUID FROM Token " +
                    "WHERE Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            if(rs.getString("UserUUID").equals(UserUUID) && rs.getString("TokenUUID").equals(token)) {
                return true;
            }
            return false;
        }catch(Exception e){
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
            return false;
        }
    }

    public String UserUUIDFromEmail(String email){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT User.UserUUID FROM User WHERE User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString("UserUUID");
        }catch(Exception e){
            System.out.println("UserDBChecker : UserUUIDFromEmail");
            System.out.println(e);
            return null;
        }
    }

    public void clearTestUsers(){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM User WHERE User.email like '%%InternalTesting.com'");
            statement.executeUpdate(query);
        }catch(Exception e){
            System.out.println("\nUserDBChecker : clearTestUsers");
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }
}
