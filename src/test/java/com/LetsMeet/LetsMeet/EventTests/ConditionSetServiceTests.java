package com.LetsMeet.LetsMeet.EventTests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.DAO.ConditionSetDao;
import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Service.ConditionSetService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConditionSetServiceTests {

    @Autowired
    ConditionSetService service;

    @Autowired
    ConditionSetDao dao;
    
    @Test
    @Order(1)
    public void createDefault(){
        ConditionSet set = service.createDefault();
        assertEquals("event_time", set.getVariable("event_time").getKey());
        assertEquals("event_service", set.getVariable("event_service").getKey());
        service.delete(set.getUUID());
    }

    @Test
    @Order(2)
    public void delete(){
        ConditionSet set = service.createDefault();
        assertTrue(dao.get(set.getUUID()).isPresent());
        service.delete(set.getUUID());
        assertFalse(dao.get(set.getUUID()).isPresent());
    }

    @Test
    @Order(3)
    public void addPeriod(){

        // Create a conditionset
        ConditionSet set = service.createDefault();

        // Add period to condition
        List<DateTimeRange> values= new ArrayList<>();
        DateTimeRange date = new DateTimeRange(new Date(2002, 01, 9), new Date(2021, 02, 12));
        values.add(date);
        service.addTimeRanges(set.getUUID(), values);

        // Check it saves/loads correct
        assertEquals(date.getEnd(), service.getTimeRange(set.getUUID()).get().get(0).getEnd());
        assertEquals(date.getStart(), service.getTimeRange(set.getUUID()).get().get(0).getStart());

        // Delete it
        service.delete(set.getUUID());
        assertFalse(dao.get(set.getUUID()).isPresent());
    }
}
