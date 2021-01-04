package com.LetsMeet.Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsModel {

    Connection con;

    public EventsModel(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522", "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public String NewEvent(String UUID, String name, String desc, String location ){

        System.out.println(UUID);

        try{
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO Event (EventUUID, Name, Description, Location) VALUES (?,?,?,?)");
            statement.setString(1, UUID);
            statement.setString(2, name);
            statement.setString(3, desc);
            statement.setString(4, location);

            int rows = statement.executeUpdate();

            if(rows > 0){
                return "Event Created Successfully";
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println(e);
            return "Error creating event";
        }
    }

}
