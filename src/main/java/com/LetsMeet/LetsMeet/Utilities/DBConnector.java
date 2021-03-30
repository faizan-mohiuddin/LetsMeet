package com.LetsMeet.LetsMeet.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
/**
 * @deprecated (Replaced with Utilities.DatabaseInterface. Please switch to this new class at nearest convenience)
 */
@Deprecated(forRemoval = true)
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
        this.con = DatabaseInterface.get();
    }

    @Deprecated // use DatabaseInterface .drop() instead
    public void close(){
        DatabaseInterface.drop();
    }
}



