package com.LetsMeet.LetsMeet.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DBConnector {

    @Autowired
    LetsMeetConfiguration config;

    Connection con;

    public Connection getCon(){
        return con;
    }

    public void open() {
        try {
            System.out.println("Connecting to " + config.getDatabaseName() + "@" + config.getDatabaseHost());
            
            this.con = DriverManager.getConnection(this.config.getDatabaseHost() + "/" + this.config.getDatabaseName(),
                    this.config.getDatabaseUser(), this.config.getDatabasePassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            this.con.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}



