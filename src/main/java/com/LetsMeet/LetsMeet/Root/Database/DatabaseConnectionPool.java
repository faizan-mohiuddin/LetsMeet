package com.LetsMeet.LetsMeet.Root.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    private boolean RUN = true;

    // Get a logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    @Autowired
    private LetsMeetConfiguration config;

    private int connectionTarget = 20;
    private int connectionLimit = 50;

    private Queue<Connection> readyConnectionPool;
    private Queue<Connection> usedConnectionPool;

    private Queue<Connection> idleBuffer = new LinkedList<>();
    private Queue<Connection> usedBuffer = new LinkedList<>();
    private Queue<Connection> swapBuffer =  new LinkedList<>();

    // Thread asynchronously checks each connection object and replaces those which are no longer valid
    Thread validator = new Thread(() -> {

        while (RUN){
            Instant start = Instant.now();
            try{

                // Create buffers
                usedBuffer = new LinkedList<>(usedConnectionPool);
                usedConnectionPool.clear(); // Potential to loose some connections in time between these two methods

                int failed = 0;

                // Check return queue
                for (Connection c : usedBuffer){
                if (c == null)  failed++;                                                                           // Discard invalid connections
                else if (c.isValid(1) && idleBuffer.size()<=connectionTarget)   idleBuffer.add(c);           // Add valid connections into ready queue
                else c.close();                                                                                     // Discard unwanted connections
                } 

                // Check ready queue
                Iterator<Connection> iti = idleBuffer.iterator();
                while (iti.hasNext()){
                    Connection c = iti.next();      
                    if (!c.isValid(1)){
                        c.close();
                        failed++;
                        iti.remove();
                    }
                }

                // Replace failed connection with new ones
                for (int i = 0; i < failed; i++){
                    idleBuffer.add(openConnection(10));
                }

                // Swap buffers
                swapBuffer = readyConnectionPool;
                readyConnectionPool = idleBuffer;
                idleBuffer = swapBuffer;

                // Report high failure rate
                double failedPercentage = failed/((double) readyConnectionPool.size()+0.001);
                if (failedPercentage > 0.25)
                    LOGGER.warn("High proportion of failed connections detected  ({}%)", failedPercentage);
                
                // Sleep if possible
                long time = VALIDATOR_FREQUENCY - Duration.between(start, Instant.now()).toMillis();
                if (time > 0)
                    Thread.sleep(time);
                else
                    LOGGER.warn("connection validation backlog - Consider reducing the number of concurrent connections. [time overflow ={}ms]", -time);
            }
            catch(Exception e){
                LOGGER.warn("Connection Validator worker thread encountered an issue: {}", e.getMessage());
            }
        }

        Queue<Connection> toClose = new LinkedList<>();
        toClose.addAll(idleBuffer);
        toClose.addAll(usedBuffer);
        toClose.addAll(swapBuffer);
        toClose.addAll(readyConnectionPool);

        int closedCount = 0;
        int errorCount = 0;

        for (Connection c : toClose){
            try {
                c.close();
                closedCount++;
            } catch (SQLException throwables) {
                errorCount++;
            }
        }
        LOGGER.info("Closed {} connections gracefully. {} connections could not be closed", closedCount, errorCount);

    });

    Thread stop = new Thread(() -> {
        this.RUN = false;
        try {
            validator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    });


    @PostConstruct
    public void Setup() {
        readyConnectionPool = new LinkedList<>();
        usedConnectionPool = new LinkedList<>();

        connectionTarget = config.getconnectionTarget();
        connectionLimit = config.getconnectionLimit();

        if (connectionLimit < 1) {
            connectionLimit = POOL_SIZE;
            LOGGER.warn("Property lm.config.connectionLimit is not valid. Set to default of {}", POOL_SIZE);
        }

        try {
            for (int i = 0; i < connectionLimit; i++) {
                readyConnectionPool.add(openConnection(0));
            }
        } catch (Exception e) {
            LOGGER.error("Database Service did not start correctly: {}", e.getMessage());
        }

        validator.start();

        Runtime.getRuntime().addShutdownHook(stop);

    }
    /**
     * It is safe to assume that the connection returned is valid
     * @return a connection object from the connection pool
     */
    public Connection get() {
        try{
        
        return readyConnectionPool.remove();
        }
        catch (Exception e){
            if (readyConnectionPool.peek() == null){try {
                return openConnection(0);
            } catch (Exception e1) {
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
        usedConnectionPool.add(connection);
    }

    private Connection openConnection(int attempts) throws IOException, InterruptedException {
        try {
            return DriverManager.getConnection(config.getDatabaseHost() + "/" + config.getDatabaseName(), config.getDatabaseUser(), config.getDatabasePassword());
        } 
        catch (SQLException e) {
            if (attempts > 0){
                throw new IOException("Connection was not established after "+ attempts + "attempts: " + e.getSQLState());
            }
            else{
                LOGGER.warn("Connection failed - Trying again: Attempt {}/10", attempts);
                Thread.sleep(1000);
                return openConnection(--attempts);
            }
            
        }
    }
}
