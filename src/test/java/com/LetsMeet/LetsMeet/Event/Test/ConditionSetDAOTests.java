//package com.LetsMeet.LetsMeet.Event.EventTests;
//
//import com.LetsMeet.LetsMeet.Event.DAO.ConditionSetDao;
//import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
//import com.LetsMeet.LetsMeet.Event.Model.Variable;
//
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.Assert.assertSame;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Date;
//import java.util.UUID;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class ConditionSetDAOTests {
//
//    @Autowired
//    ConditionSetDao dao;
//
//    static UUID dummyID = UUID.fromString("807e9309-5cb6-4bd6-8909-1a655b950ca2");
//
//    @Test
//    @Order(1)
//    public void createConditionSet(){
//        assertTrue(dao.save(createDummyConditionSet()));
//    }
//
//    @Test
//    @Order(2)
//    public void getConditionSet(){
//        assertTrue(dao.get(dummyID).isPresent());
//        ConditionSet remote = dao.get(dummyID).get();
//        ConditionSet local = createDummyConditionSet();
//
//        assertEquals(local.getName(), remote.getName());
//        assertEquals(local.getVariable("dates").getDomain(), remote.getVariable("dates").getDomain());
//        assertEquals(local.getVariable("test").getDomain(), remote.getVariable("test").getDomain());
//        assertEquals(local.getClass(), remote.getClass());
//
//
//        //assertEquals(dummyID, dao.get(dummyID).get().getUUID());
//    }
//
//    @Test
//    @Order(3)
//    public void updateConditionSet(){
//        String newName = "test_nameChange";
//        String[] words = {"foo", "bar"};
//        ConditionSet newConditionSet = createDummyConditionSet();
//        newConditionSet.setName(newName);
//        newConditionSet.addVariable( new Variable<String>("test_2", words));
//
//        assertTrue(dao.update(newConditionSet));
//        assertEquals(newName, dao.get(dummyID).get().getName());
//        assertEquals(words[0], dao.get(dummyID).get().getVariable("test_2").getDomain().get(0));
//        assertEquals(words[1], dao.get(dummyID).get().getVariable("test_2").getDomain().get(1));
//    }
//
//    @Test
//    @Order(4)
//    public void deleteConditionSet(){
//        assertTrue(dao.delete(dummyID));
//        assertFalse(dao.get(dummyID).isPresent());
//    }
//
//
//
//
//    private ConditionSet createDummyConditionSet(){
//        ConditionSet set = new ConditionSet(dummyID);
//        Integer[] nums = {1,2,3,4};
//        Date[] domain = {new Date(2020, 1, 1), new Date(2020, 3, 8)};
//        set.addVariable(new Variable<Integer>("test", nums));
//        set.addVariable(new Variable<Date>("dates", domain));
//        //set.getVariable("test").append(10);
//        return set;
//    }
//
//}
//
//
