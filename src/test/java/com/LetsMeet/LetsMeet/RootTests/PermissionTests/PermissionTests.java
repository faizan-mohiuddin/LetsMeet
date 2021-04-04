package com.LetsMeet.LetsMeet.RootTests.PermissionTests;

import com.LetsMeet.LetsMeet.Root.Database.DatabaseConnectionPool;
import com.LetsMeet.LetsMeet.Root.Permission.PermissionDAO;
import com.LetsMeet.LetsMeet.Root.Permission.PermissionService;
import com.LetsMeet.LetsMeet.Root.Permission.Model.Permissions;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PermissionTests {

    @Autowired
    DatabaseConnectionPool databaseService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    PermissionDAO permissionDAO;


    @Test
    @Order(1)
    public void create() throws SQLException{
        var x = Permissions.create(UUID.randomUUID(), UUID.randomUUID(), "0300");
        permissionService.setPermission(x);

        assertFalse(permissionService.getPermission(x.getParent(), x.getChild(), "0400"));
        assertTrue(permissionService.getPermission(x.getParent(), x.getChild(), "0300"));
        
        permissionService.clearPermission(x);
        assertFalse(permissionService.getPermission(x.getParent(), x.getChild(), "0300"));
    }
    
}
