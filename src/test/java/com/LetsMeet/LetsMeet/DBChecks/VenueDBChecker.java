package com.LetsMeet.LetsMeet.DBChecks;

import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class VenueDBChecker {
    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DBConnector database;

    public String venueUUIDFromNameandBusinessUUID(String name, String businessUUID){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT Venue.VenueUUID FROM Venue, HasVenue WHERE Venue.Name = '%s' AND " +
                    "HasVenue.BusinessUUID = '%s' AND HasVenue.VenueUUID = Venue.VenueUUID", name, businessUUID);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String response = rs.getString(1);
            database.close();
            return response;
        }catch(Exception e){
            database.close();
            System.out.println("\nVenueDBChecker: venueUUIDFromNameandBusinessUUID");
            System.out.println(e);
            return null;
        }
    }
}
