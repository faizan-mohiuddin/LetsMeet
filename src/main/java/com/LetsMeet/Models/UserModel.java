package com.LetsMeet.Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    Connection con;

    public UserModel(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522",
                    "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println("\nUser Model: initilise");
            System.out.println(e);
        }
    }

    public String newUser(String UUID, String fName, String lName, String email, String passwordhash, String salt ){
        try{
            PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO User (UserUUID, fName, lName, email, PasswordHash, salt) VALUES (?,?,?,?,?,?)");
            statement.setString(1, UUID);
            statement.setString(2, fName);
            statement.setString(3, lName);
            statement.setString(4, email);
            statement.setString(5, passwordhash);
            statement.setString(6, salt);
            int rows = statement.executeUpdate();

            if(rows > 0){
                return "User Account Created Successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("\nUser Model: newUser");
            System.out.println(e);
            return "Error creating account";
        }
    }

    public UserData getUserByEmail(String email){
        UserData user = new UserData();

        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from User where User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            user.populate(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6));

            return user;

        }catch(Exception e){
            System.out.println("\nUser Model: getUserByEmail");
            System.out.println(e);
            return null;
        }
    }

    public UserData getUserByUUID(String uuid){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from User where User.UserUUID = '%s'", uuid);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            UserData user = new UserData();
            user.populate(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6));

            return user;

        }catch(Exception e){
            System.out.println("\nUser Model: getUserByUUID");
            System.out.println(e);
            return null;
        }
    }

    public boolean CheckUserToken(String UUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT COUNT(TokenUUID) AS Tokens FROM Token WHERE Token.UserUUID = '%s'", UUID);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            int count = rs.getInt(1);

            if(count > 0){
                return true;
            }else{
                return false;
            }

        }catch(Exception e){
            System.out.println("\nUser Model: CheckUserToken");
            System.out.println(e);
            return false;
        }
    }

    public String createToken(String UUID, String Token, long expires){
        try{
            PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO Token (UserUUID, TokenUUID, Expires) VALUES (?, ?, ?)");
            statement.setString(1, UUID);
            statement.setString(2, Token);
            statement.setLong(3, expires);
            int rows = statement.executeUpdate();

            if(rows > 0){
                return "Token created successfully";
            }else{
                throw new Exception("Error creating token");
            }

        }catch(Exception e){
            System.out.println(e);
            return "Error creating API token";
        }
    }

    public void removeAllUserToken(String UUID){
        // Remove all tokens corresponding to a user
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM Token WHERE Token.UserUUID = '%s'", UUID);
            statement.executeUpdate(query);

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public String populateHasUsers(String eventUUID, String userUUID, boolean IsOwner){
        try{
            PreparedStatement statement = this.con.prepareStatement(
                    "INSERT INTO HasUsers (EventUUID, UserUUID, IsOwner) VALUES (?, ?, ?)");
            statement.setString(1, eventUUID);
            statement.setString(2, userUUID);
            statement.setBoolean(3, IsOwner);

            int rows = statement.executeUpdate();

            if(rows > 0){
                return "Link added successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("\nUser Model: populateHasUsers");
            System.out.println(e);
            return null;
        }
    }

    public TokenData getTokenRecord(String token){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from Token where Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            TokenData data = new TokenData();
            data.populate(rs.getString(1), rs.getString(2), rs.getInt(3));
            return data;

        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public boolean checkEmailExists(String email){
        // Returns true if the email exists
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT COUNT(email) AS Emails FROM User WHERE User.email = '%s'", email);

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            int count = rs.getInt(1);

            if(count > 0){
                return true;
            }
            return false;

        }catch(Exception e){
            System.out.println("Event Model: checkEmailExists");
            System.out.println(e);
            return true;
        }
    }
}
