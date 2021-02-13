package com.LetsMeet.LetsMeet;

import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventTests {

    @Autowired
    EventService event;

    @Autowired
    EventDao eventDao;

    Event testEvent = new Event(
        "807e9309-5cb6-4bd6-8909-1a655b950ca2",
        RandomStringUtils.randomAlphabetic(8),
        RandomStringUtils.randomAlphabetic(8),
        RandomStringUtils.randomAlphabetic(8),
        new ConditionSet(UUID.randomUUID().toString()));

    @Test
    @Order(1)
    public void createEvent(){
        assertTrue(eventDao.save(testEvent));
    }

    @Test
    @Order(2)
    public void getEvent(){
    assertTrue(eventDao.get(testEvent.getUUID()).isPresent());
    }

    @Test
    @Order(3)
    public void getAllEvents(){
    assertTrue(eventDao.getAll().isPresent());
    }

    @Test
    @Order(4)
    public void deleteEvent(){
    assertTrue(eventDao.delete(testEvent.getUUID()));
    }
    
}
