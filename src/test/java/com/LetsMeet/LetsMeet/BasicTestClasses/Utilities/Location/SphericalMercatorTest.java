package com.LetsMeet.LetsMeet.BasicTestClasses.Utilities.Location;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Service.EventTimeSolver;
import com.LetsMeet.LetsMeet.Utilities.Location.SphericalMercator;
import com.LetsMeet.LetsMeet.Utilities.Model.EntityProperties;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SphericalMercatorTest {

    @Test
    @Order(1)
    public void basictest(){
        SphericalMercator sm = new SphericalMercator();
        
        System.out.println(sm.xAxisProjection(57.1515141));
        //solver.solve(5);
        //System.out.println(solver.solve(5));
        //EventTimeSolver.withDuration(solver.solve(5), Duration.ofDays(35));
        //EventTimeSolver.withResponses(solver.solve(5), createTestResponse(1));
        assertTrue(true);
    }
}
