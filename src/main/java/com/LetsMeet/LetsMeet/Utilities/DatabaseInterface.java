//-----------------------------------------------------------------
// DatabaseInterface.java
// Let's Meet 2021
//
/* Maintains a static reference to a single database and the number of current users.
Database connection is closed if the number of users drops below the minimum threshold  */

package com.LetsMeet.LetsMeet.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//-----------------------------------------------------------------

@Service
public class DatabaseInterface{

    // Get a logger
    private static final Logger LOGGER=LoggerFactory.getLogger(DatabaseInterface.class);

    // Allocate space for config class
    private static LetsMeetConfiguration config;

    // Get instance of config class and allocate to static storage
    @Autowired
    private DatabaseInterface(LetsMeetConfiguration config){
        DatabaseInterface.config = config;
    }

    // The database connection
    private static Connection connection;
    private static long ts;

    // database users
    private static int users = 0;
    private static int STAY_ALIVE_THRESHOLD = 10;


    private static Boolean openConnection(){
        try{
            DatabaseInterface.connection = DriverManager.getConnection(config.getDatabaseHost() + "/" + config.getDatabaseName(), config.getDatabaseUser(), config.getDatabasePassword());
            LOGGER.info("Established connection to {} @ {}", config.getDatabaseName(), config.getDatabaseHost());
            ts = System.currentTimeMillis()/1000;
            return true;
        }
        catch(Exception e){
            LOGGER.error("Could not connect to database: {}", e.getMessage());
            return false;
        }
    }

    private static Boolean closeConnection(){
        try{
            connection.close();
            return true;       
        }
        catch(Exception e){
            LOGGER.error("Could not close database: {}", e.getMessage());
            return false;
        }
    }

    // Initialises a connection if required and returns reference to it
    public static Connection get(){
        try{
            if (users < 0 || connection == null || (System.currentTimeMillis()/1000)-ts > 600000 ||connection.isClosed()){openConnection();}
            users++;
            return connection;
        }
        catch (Exception e){
            LOGGER.error("Could not get database connection: {}", e.getMessage());
            return null;
        }
    }

    // Decrement the user count and if it is bellow the threshold, drop the connection
    public static void drop(){
        users--;
        if (users < -STAY_ALIVE_THRESHOLD){closeConnection();}
    }
}
