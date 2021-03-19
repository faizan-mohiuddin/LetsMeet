package com.LetsMeet.LetsMeet.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

    /**
* Java >= 9
* @deprecated (when, why, use static singleton method DatabaseInterface.get())
*/

@Service
@Deprecated(forRemoval = true)
public class DBConnector {

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DatabaseInterface database;

    public Connection con;

    public Connection getCon(){
        return con;
    }

    public void open() {
        this.con = DatabaseInterface.get();
    }



    public void close(){
        DatabaseInterface.drop();
    }
}



