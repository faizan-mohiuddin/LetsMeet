package com.LetsMeet.LetsMeet.EventTests;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Service.EventLocationSolver;
import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventLocationSolverTests {

    String TEST_UUID = "874fc6b0-15d9-45ae-bef0-e7467c254e3c";

    private Event createTestEvent(){
        return new Event(UUID.randomUUID(), "name", "desc", "location", new EntityProperties(), new EventProperties(new ArrayList<>(), new ArrayList<>(), new Location("l_event", 57.1461249, -2.1241101, 8046)), new Poll());
    }

    private ArrayList<EventResponse> createTestResponse(int n){
        ArrayList<EventResponse> responses = new ArrayList<>();
        for (int i = 0; i < n; i++){
            responses.add(new EventResponse(UUID.fromString(TEST_UUID), UUID.fromString(TEST_UUID), new EventProperties(new ArrayList<>(),new ArrayList<>() , new Location("l_1" + i, 57.156879, -2.0950873, 8046))));
            responses.add(new EventResponse(UUID.fromString(TEST_UUID), UUID.fromString(TEST_UUID), new EventProperties(new ArrayList<>(),new ArrayList<>() , new Location("l_2" + i, 57.156879, -2.1241101, 5))));
        }
        return responses;
    }
    
    @Test
    @Order(1)
    public void basictest(){
        EventLocationSolver solver = new EventLocationSolver(createTestEvent().getEventProperties(), createTestResponse(1));
        solver.solve();
        assertTrue(true);
    }

    @Test
    @Order(2)
    public void sortLocations(){
        Location[] locations = {
            new Location("base", 0.0, 0.0, 5),
            new Location("ne", 1.0, 1.0, 5),
            new Location("se", 1.0, -1.0, 5),
            new Location("ne", -1.0, 1.0, 5),
            new Location("sw", -1.0, -1.0, 5)
        };
        //Arrays.asList(locations).sort(c);

        assertSame("Is before (y-intersection at -2,  base y-intersection at 0)", -1, locations[0].compareTo(locations[1]));
        assertSame("Is same (y-intersection at 0m  base y-intersection at 0)", 0, locations[0].compareTo(locations[2]));
        assertSame("Is same (y-intersection at 0,  base y-intersection at 0)", 0, locations[0].compareTo(locations[3]));
        assertSame("Is after (y-intersection at 2, base y-intersection at 0)", 1, locations[0].compareTo(locations[4]));
        
        Arrays.sort(locations);
        assertSame("Sorted by x,y coordinate position 0 in array","sw", locations[0].getName());
        assertSame("Sorted by x,y coordinate position 4 in array","ne", locations[4].getName());
    }

    @Test
    @Order(3)
    public void mercatorTest(){
        EventLocationSolver solver = new EventLocationSolver(createTestEvent().getEventProperties(), createTestResponse(1));
        Location location = new Location("name", -2.0939613, 57.1501814, 5);
        Location location2 = new Location("name", -2.0990288, 57.1536607, 5);
        System.out.println(solver.distance(location, location2));
        assertTrue(true);
    }
}
