//package com.LetsMeet.LetsMeet.EventTests;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.LetsMeet.LetsMeet.Event.DAO.ConditionSetDao;
//import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
//import com.LetsMeet.LetsMeet.Event.Model.Properties.*;
//import com.LetsMeet.LetsMeet.Event.Service.ConditionSetService;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class ConditionSetServiceTests {
//
//    @Autowired
//    ConditionSetService service;
//
//    @Autowired
//    ConditionSetDao dao;
//
//    @Test
//    @Order(1)
//    public void createDefault(){
//        ConditionSet set = service.createDefault();
//        assertEquals("event_time", set.getVariable("event_time").getKey());
//        assertEquals("event_service", set.getVariable("event_service").getKey());
//        service.delete(set.getUUID());
//    }
//
//    @Test
//    @Order(2)
//    public void delete(){
//        ConditionSet set = service.createDefault();
//        assertTrue(dao.get(set.getUUID()).isPresent());
//        service.delete(set.getUUID());
//        assertFalse(dao.get(set.getUUID()).isPresent());
//    }
//
//    @Test
//    @Order(3)
//    public void addTime(){
//
//        // Create a conditionset
//        ConditionSet set = service.createDefault();
//
//        // Add period to condition
//        List<DateTimeRange> values= new ArrayList<>();
//        DateTimeRange date = new DateTimeRange(ZonedDateTime.parse("2019-05-03T10:15:30+01:00[Europe/Paris]"),ZonedDateTime.parse("2019-05-03T12:15:30+01:00[Europe/London]"));
//        values.add(date);
//        service.addTimeRanges(set, values);
//
//        assertTrue(date.getStart().isBefore(date.getEnd()));
//
//
//        // Check it saves/loads correct
//        assertEquals(date.getEnd(), service.getTimeRange(set).get().get(0).getEnd());
//        assertEquals(date.getStart(), service.getTimeRange(set).get().get(0).getStart());
//
//        // Delete it
//        service.delete(set.getUUID());
//        assertFalse(dao.get(set.getUUID()).isPresent());
//    }
//}
