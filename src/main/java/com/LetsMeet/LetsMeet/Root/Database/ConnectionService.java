package com.LetsMeet.LetsMeet.Root.Database;

import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Utility to generate an autoclosing connection wrapper for a connection obtained from DatabaseConnectionPool
 * @author Hamish Weir (signal 32)
 */
@Service
public class ConnectionService {
    
    @Autowired
    DatabaseConnectionPool databaseConnectionPool;

    /**
     * Creates a valid Connection wrapper from the DatabaseConnectionPool
     * @return a new DatabaseConnector with valid Connection object. Must be closed to release it back to the connection pool
     */
    public DatabaseConnector get(){
        return new DatabaseConnector(databaseConnectionPool.get(), databaseConnectionPool);
    }
}
