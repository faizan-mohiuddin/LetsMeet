package com.LetsMeet.Models;

import jdk.jfr.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsModel {

    Connection con;

    public EventsModel(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522", "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println("\nEvents Model: Initialise");
            System.out.println(e);
        }
    }

    public String NewEvent(String UUID, String name, String desc, String location ){
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
            System.out.println("\nEvents Model: NewEvent");
            System.out.println(e);
            return null;
        }
    }

    public List<EventData> allEvents(){
        try{
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("select * from Event");

            List<EventData> events = new ArrayList<>();

            while(rs.next()){
                EventData event = new EventData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                events.add(event);
            }
            return events;

        }catch(Exception e){
            System.out.println("\nEvents Model: allEvents");
            System.out.println(e);
            return null;
        }
    }

    public EventData getEventByUUID(String UUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select * from Event where Event.EventUUID = '%s'", UUID);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            EventData event = new EventData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return event;

        }catch(Exception e){
            System.out.println("\nEvents Model: getEventByUUID");
            System.out.println(e);
            return null;
        }
    }

    public String UserUUIDFromToken(String token){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("select UserUUID from Token where Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString(1);
        }catch(Exception e){
            System.out.println("\nEvents Model: UserUUIDFromToken");
            System.out.println(e);
            return null;
        }
    }

}
