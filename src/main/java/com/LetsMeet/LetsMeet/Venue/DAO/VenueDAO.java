package com.LetsMeet.LetsMeet.Venue.DAO;

import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.lang.Math;

@Component
public class VenueDAO implements DAO<Venue> {

    private final double p = Math.PI/180;  // Used for calculating distance between 2 sets or longitude and latitude
    private static final Logger LOGGER= LoggerFactory.getLogger(VenueDAO.class);

    @Autowired
    DBConnector database;

    @Override
    public Optional<Venue> get(UUID uuid) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from Venue where Venue.VenueUUID = '%s'", uuid.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<Venue> response = Optional.of(new Venue(rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6)));
            database.close();
            return response;

        }catch(Exception e){
            database.close();
            System.out.println("\nVenue Dao: get (UUID)");
            System.out.println(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<Venue>> getAll() {
        try(Statement statement = DatabaseInterface.get().createStatement()){
            ResultSet rs = statement.executeQuery("select * from Venue");
            List<Venue> venues = new ArrayList<>();

            while (rs.next()){
                venues.add(new Venue(
                        rs.getString("VenueUUID"),
                        rs.getString("Name"),
                        rs.getString("Facilities"),
                        rs.getString("Address"),
                        rs.getString("Longitude"),
                        rs.getString("Latitude")));
            }
            DatabaseInterface.drop();
            return Optional.ofNullable(venues);

        }catch(Exception e){
            System.out.println("\nVenue Dao: getALL");
            DatabaseInterface.drop();
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Boolean save(Venue venue) {
        database.open();

        // Save the event
        try{
            PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Venue (VenueUUID, Name, " +
                            "Facilities, Address, Longitude, Latitude) VALUES (?,?,?,?,?,?)");

            statement.setString(1, venue.getUUID().toString());
            statement.setString(2, venue.getName());
            statement.setString(3, venue.getJsonFacilities());
            statement.setString(4, venue.getAddress());
            statement.setDouble(5, venue.getLongitude());
            statement.setDouble(6, venue.getLatitude());

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Venue Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean update(Venue venue) {
        try{
            PreparedStatement statement = DatabaseInterface.get().prepareStatement("UPDATE Venue SET Name = ?, " +
                    "Facilities = ?, Address = ?, Longitude = ?, Latitude = ? WHERE VenueUUID = ?");
            statement.setString(1, venue.getName());
            statement.setString(2, venue.getJsonFacilities());
            statement.setString(3, venue.getAddress());
            statement.setDouble(4, venue.getLongitude());
            statement.setDouble(5, venue.getLatitude());

            statement.setString(6, venue.getUUID().toString());

            if(statement.executeUpdate() > 0){
                DatabaseInterface.drop();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Venue Dao : update");
            DatabaseInterface.drop();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean delete(Venue venue) {
        return this.delete(venue.getUUID());
    }

    @Override
    public Boolean delete(UUID uuid) {
        database.open();
        try(Statement statement = database.con.createStatement()){

            String query;
            String venueUUID = uuid.toString();

            query = String.format("DELETE FROM Venue where Venue.VenueUUID = '%s'", venueUUID);
            statement.executeUpdate(query);

            return true;

        }catch(Exception e){
            System.out.println("Venue Dao: delete (UUID)");
            database.close();
            e.printStackTrace();
            return false;
        }
    }

    public Optional<List<Venue>> search(String query){
        LOGGER.info("Venue Search: " + query);
        try(Statement statement = DatabaseInterface.get().createStatement()){
            ResultSet rs = statement.executeQuery(query);

            List<Venue> venues = new ArrayList<>();

            while (rs.next()) {
                venues.add(new Venue(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6)));
            }
            return Optional.of(venues);

        }catch(Exception e){
            database.close();
            System.out.println("\nVenue Dao: Search");
            System.out.println(e);
            return Optional.empty();
        }
    }

    public Optional<List<Venue>> searchByRadius(double longitude, double latitude, double kilometers){
        String query = String.format("SELECT * FROM Venue WHERE" +
                " 12742 * ASIN(SQRT(" +
                "0.5 - (COS((Venue.Latitude - %f) * %f)/2)" +
                " + COS(%f * %f) * COS(Venue.Latitude * %f) * (1 - COS((Venue.Longitude - %f) * %f))/2)) <= %f",
                latitude, this.p, latitude, this.p, this.p, longitude, this.p, kilometers);
        System.out.println(query);

        try{
            Statement statement = DatabaseInterface.get().createStatement();
            ResultSet rs = statement.executeQuery(query);

            List<Venue> venues = new ArrayList<>();
            while(rs.next()){
                venues.add(new Venue(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6)));
            }
            return Optional.of(venues);

        }catch(Exception e){
            System.out.println("\nVenue Dao: searchByRadius");
            System.out.println(e);
            return Optional.empty();
        }
    }
}