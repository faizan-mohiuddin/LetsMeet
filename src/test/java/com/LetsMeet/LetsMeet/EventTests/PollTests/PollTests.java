package com.LetsMeet.LetsMeet.EventTests.PollTests;

import com.LetsMeet.LetsMeet.Event.Poll.*;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Polls;
import com.LetsMeet.LetsMeet.Root.Database.DatabaseConnectionPool;
import com.LetsMeet.LetsMeet.Root.Permission.PermissionDAO;
import com.LetsMeet.LetsMeet.Root.Permission.PermissionService;
import com.LetsMeet.LetsMeet.Root.Permission.Model.Permissions;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PollTests {

    @Autowired
    PollService pollService;

    @Autowired 
    PollDAO pollDAO;

    private User testUser(){
        return new User("48f9f376-0dc0-38e4-bae9-f4e50f5f73db");
    }

    @Test
    @Order(1)
    public void create() throws IOException{
        List<String> options = List.of("option1", "option2", "option3");
        List<String> choices1 = List.of("option2");
        List<String> choices2 = List.of("option2", "option1");
        List<String> choices3 = List.of("option3");
        var poll = Polls.of(options, true, "test poll");
        pollService.create(testUser(), poll);

        pollService.addResponse(poll, choices1);
        pollService.addResponse(poll, choices2);
        pollService.addResponse(poll, choices3);

        var pol2 = pollService.getPoll(poll.getUUID());
        pol2.getOptions();
        pollDAO.delete(poll);

    }
    
}
