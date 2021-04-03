package com.LetsMeet.LetsMeet.Root.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements a pool of abstract connection objects which can be requested get() and given back give()
 * @author Hamish Weir (signal32)
 */
@Service
public class DatabaseConnectionPool {

    private int POOL_SIZE = 4;
    private int VALIDATOR_FREQUENCY = 5000;

    // Get a logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    @Autowired
    private LetsMeetConfiguration config;

    private Queue<Connection> idleConnectionPool;
    private Queue<Connection> returnConnectionPool;

    // Thread asynchronously checks each connection object and replaces those which are no longer valid
    Thread validator = new Thread(() -> {
        while (POOL_SIZE > 0){
            Instant start = Instant.now();
            try{
                Iterator<Connection> it = returnConnectionPool.iterator();
                while (it.hasNext()){
                    Connection c = it.next();
                    it.remove();
                    if (c.isValid(1)){idleConnectionPool.add(c);}
                    else {idleConnectionPool.add(openConnection());}
                }
                
                long time = VALIDATOR_FREQUENCY - Duration.between(start, Instant.now()).toMillis();
                if (time > 0){Thread.sleep(time);}
                else
                    LOGGER.warn("connection validation backlog - Consider reducing the number of concurrent connections. [time overflow ={}ms]", -time);
            }catch(Exception e){
                LOGGER.warn("Connection Validator worker thread encountered an issue: {}", e.getMessage());
            }
        }
    });


    @PostConstruct
    public void Setup() {
        idleConnectionPool = new LinkedList<>();
        returnConnectionPool = new LinkedList<>();

        int connectionLimit = config.getconnectionLimit();
        if (connectionLimit < 1) {
            connectionLimit = POOL_SIZE;
            LOGGER.warn("Property lm.config.connectionLimit is not valid. Set to default of {}", POOL_SIZE);
        }

        try {
            for (int i = 0; i < connectionLimit; i++) {
                idleConnectionPool.add(openConnection());
            }
        } catch (Exception e) {
            LOGGER.error("Database Service did not start correctly: {}", e.getMessage());
        }

        validator.start();

    }
    /**
     * It is safe to assume that the connection returned is valid
     * @return a connection object from the connection pool
     */
    public Connection get() {
        try{
        
        return idleConnectionPool.remove();
        }
        catch (Exception e){
            if (idleConnectionPool.peek() == null){try {
                return openConnection();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            }}
            return null;
        }
    }

    /**
     * Returns connection object to the inbound connection queue. After validation it will become available in the connection pool.
     * @param connection
     */
    public void give(Connection connection){
        returnConnectionPool.add(connection);
    }

    private Connection openConnection() throws IOException {
        try {
            return DriverManager.getConnection(config.getDatabaseHost() + "/" + config.getDatabaseName(),
                    config.getDatabaseUser(), config.getDatabasePassword());
        } catch (SQLException e) {
            throw new IOException("Connection was not established: " + e.getSQLState());
        }
    }
}
