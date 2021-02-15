package com.LetsMeet.LetsMeet;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BusinessVenueTests {
    @Test
    @Order(1)
    public void createBusiness(){

    }

    @Test
    @Order(2)
    public void deleteBusiness(){

    }

    @Test
    @Order(3)
    public void editBusiness(){

    }

    @Test
    @Order(4)
    public void createVenue(){

    }

    @Test
    @Order(5)
    public void deleteVenue(){

    }

    @Test
    @Order(6)
    public void editVenue(){

    }

    @Test
    @Order(50)
    public void cleanup(){

    }
}
