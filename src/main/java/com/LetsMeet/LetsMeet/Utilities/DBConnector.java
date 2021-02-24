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

    @Autowired
    DatabaseInterface database;

    public Connection con;

    public Connection getCon(){
        return con;
    }
    @Deprecated // use DatabaseInterface .get() instead
    public void open() {
        //database.openConnection();
        this.con = database.get();
    }

    @Deprecated // use DatabaseInterface .drop() instead
    public void close(){
        database.drop();
    }
}



