package com.LetsMeet.LetsMeet.EventTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Service.EventTimeSolver;
import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventTimeSolverTests {

    String TEST_UUID = "874fc6b0-15d9-45ae-bef0-e7467c254e3c";

    private Event createTestEvent(){
        ArrayList<DateTimeRange> times = new ArrayList<>();
        times.add(new DateTimeRange(ZonedDateTime.parse("2000-01-01T12:00:00+01:00") , ZonedDateTime.parse("2000-05-01T12:00:00+01:00")));
        times.add(new DateTimeRange(ZonedDateTime.parse("2000-07-01T12:00:00+01:00") , ZonedDateTime.parse("2000-09-01T12:00:00+01:00")));
        times.add(new DateTimeRange(ZonedDateTime.parse("2000-09-01T12:00:00+01:00") , ZonedDateTime.parse("2000-12-01T12:00:00+01:00")));
        return new Event(UUID.randomUUID(), "name", "desc", "location", new EntityProperties(), new EventProperties(times, new ArrayList<>(), null), new Poll());
    }

    private ArrayList<EventResponse> createTestResponse(int n){
        ArrayList<EventResponse> responses = new ArrayList<>();
        for (int i = 0; i < n; i++){
            ArrayList<DateTimeRange> times = new ArrayList<>();
            times.add(new DateTimeRange(ZonedDateTime.parse("2000-02-01T12:00:00+01:00") , ZonedDateTime.parse("2000-11-01T12:00:00+01:00")));
            //times.add(new DateTimeRange(ZonedDateTime.parse("2000-02-01T12:00:00+01:00") , ZonedDateTime.parse("2000-07-01T12:00:00+01:00")));
            responses.add(new EventResponse(UUID.fromString(TEST_UUID), UUID.fromString(TEST_UUID), new EventProperties(times, new ArrayList<>(), null)));
        }
        return responses;
    }
    
    @Test
    @Order(1)
    public void basictest(){
        EventTimeSolver solver = new EventTimeSolver(createTestEvent(), createTestResponse(1));
        solver.solve();
        assertTrue(true);
    }
}
