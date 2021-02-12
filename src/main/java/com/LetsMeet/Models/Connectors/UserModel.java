package com.LetsMeet.Models.Connectors;

import com.LetsMeet.Models.Data.AdminUserData;
import com.LetsMeet.Models.Data.TokenData;
import com.LetsMeet.Models.Data.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserModel extends DBConnector{

    Connection con;

    public UserModel(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://rpi2.net.hamishweir.uk:7457/letsmeet", "lmadmin_oPJQFwg4", "WSbBBz39E4kYLNkk");
        }catch(Exception e){
            System.out.println("\nUser Model: initilise");
            System.out.println(e);
            this.conRety();
        }
    }

    private void conRety(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522",
                    "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println("\nUser Model: conRetry");
            System.out.println(e);
        }
    }

    public void close(){
        try {
            this.con.close();
        }catch(Exception e){
            System.out.println("\nUser Model: closeCon");
            System.out.println(e);
            if(this.con != null) {
                this.close();
            }
        }
    }

    private boolean checkCon(){
        if(this.con == null){
            return false;
        }
        return true;
    }

    public String populateHasUsers(String eventUUID, String userUUID, boolean IsOwner){
        if(this.checkCon()) {
            try {
                PreparedStatement statement = this.con.prepareStatement(
                        "INSERT INTO HasUsers (EventUUID, UserUUID, IsOwner) VALUES (?, ?, ?)");
                statement.setString(1, eventUUID);
                statement.setString(2, userUUID);
                statement.setBoolean(3, IsOwner);

                int rows = statement.executeUpdate();

                if (rows > 0) {
                    return "Link added successfully";
                } else {
                    throw new Exception("Nothing added to DB");
                }

            } catch (Exception e) {
                System.out.println("\nUser Model: populateHasUsers");
                System.out.println(e);
                return null;
            }
        }
        return "Error connecting";
    }

    public List<AdminUserData> allUsers(){
        try{
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("select * from User");

            List<AdminUserData> users = new ArrayList<>();

            while(rs.next()){
                AdminUserData user = new AdminUserData();
                user.populate(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
                users.add(user);
            }
            return users;

        }catch(Exception e){
            System.out.println("\nUser Model: allUsers");
            System.out.println(e);
            return null;
        }
    }
}