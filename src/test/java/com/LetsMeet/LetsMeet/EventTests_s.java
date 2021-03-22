//package com.LetsMeet.LetsMeet;
//
//import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
//import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
//import com.LetsMeet.LetsMeet.Event.Model.Constraint;
//import com.LetsMeet.LetsMeet.Event.Model.Event;
//import com.LetsMeet.LetsMeet.Event.Model.Poll;
//import com.LetsMeet.LetsMeet.Event.Model.Variable;
//import com.LetsMeet.LetsMeet.Event.Service.ConditionSetService;
//import com.LetsMeet.LetsMeet.Event.Service.EventService;
//import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Date;
//import java.util.UUID;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class EventTests_s {
//
//    @Autowired
//    EventService event;
//
//    @Autowired
//    EventDao eventDao;
//
//    //@Autowired
//    //ConditionSetService conditionSetService;
//
//    Event testEvent = new Event("807e9309-5cb6-4bd6-8909-1a655b950ca2", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8), new EntityProperties(), getConditionSet().getUUID(), new Poll());
//
//    @Test
//    @Order(1)
//    public void createEvent(){
//        assertTrue(eventDao.save(testEvent));
//    }
//
//    @Test
//    @Order(2)
//    public void updateEvent(){
//    assertTrue(eventDao.update(testEvent));
//    }
//
//    //@Test
//    //@Order(3)
//    public void updateEventFromService(){
//        Variable var1 = new Variable<>(new Integer[] {1,2,3});
//        Variable var2 = new Variable<>(new Integer[] {5,6,1});
//    assertTrue(event.addVariable(testEvent.getUUID(), var1));
//    assertTrue(event.addVariable(testEvent.getUUID(), var2));
//    event.addConstraint(testEvent.getUUID(), new Constraint<Integer>(UUID.randomUUID().toString(), "myConstraint", var1, var2, '='));
//    }
//
//    @Test
//    @Order(4)
//    public void getEvent(){
//    assertTrue(eventDao.get(testEvent.getUUID()).isPresent());
//    }
//
//
//
//    @Test
//    @Order(5)
//    public void getAllEvents(){
//    assertTrue(eventDao.getAll().isPresent());
//    }
//
//    @Test
//    @Order(6)
//    public void deleteEvent(){
//    assertTrue(eventDao.delete(testEvent.getUUID()));
//    }
//
//    //Helpers
//    ConditionSet getConditionSet(){
//
//        ConditionSet set = new ConditionSet(UUID.randomUUID());
//        Integer[] nums = {1,2,3,4};
//        set.addVariable(new Variable<Integer>("test", nums));
//        return set;
//    }
//
//}
