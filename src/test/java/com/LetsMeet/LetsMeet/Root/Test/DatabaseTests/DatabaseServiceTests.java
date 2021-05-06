package com.LetsMeet.LetsMeet.Root.Test.DatabaseTests;

import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.DatabaseConnectionPool;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

import java.sql.SQLException;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseServiceTests {

    @Autowired
    DatabaseConnectionPool databaseService;

    @Autowired
    ConnectionService connectionService;

    @Autowired
    EventService events;

    @Test
    @Order(1)
    public void create() throws SQLException{
        var x = connectionService.get();
        assertTrue(!x.getConnection().isClosed());
    }

    @Test
    @Order(2)
    public void use(){
    assertNotNull(null);
    }
    
}
