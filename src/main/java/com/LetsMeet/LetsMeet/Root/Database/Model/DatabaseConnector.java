package com.LetsMeet.LetsMeet.Root.Database.Model;

import java.sql.Connection;

import com.LetsMeet.LetsMeet.Root.Database.DatabaseConnectionPool;


public class DatabaseConnector implements AutoCloseable{

    private DatabaseConnectionPool databaseService;
    
    private Connection connection;


    public DatabaseConnector(Connection connection, DatabaseConnectionPool databaseService){
        this.connection = connection;
        this.databaseService = databaseService;
    }

    @Override
    public void close() {
        databaseService.give(connection);      
    }

    public Connection getConnection() {
        return connection;
    }

}
