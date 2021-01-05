package com.LetsMeet.Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    Connection con;

    public UserModel(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522", "sql2383522", "iN8!qL4*");
        }catch(Exception e){
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
            System.out.println(e);
            return null;
        }
    }
}
