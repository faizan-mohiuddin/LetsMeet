package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Component
public class VenueDAO implements DAO<Venue> {

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
                    rs.getString(3)));
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
        return Optional.empty();
    }

    @Override
    public Boolean save(Venue venue) {
        database.open();

        // Save the event
        try{
            PreparedStatement statement;
            if(venue.numFacilities() < 1) {
                statement = database.getCon().prepareStatement("INSERT INTO Venue (VenueUUID, Name) VALUES (?,?)");
            }else{
                statement = database.getCon().prepareStatement("INSERT INTO Venue (VenueUUID, Name, Facilities) VALUES (?,?,?)");
                statement.setString(3, venue.getJsonFacilities());
            }
            statement.setString(1, venue.getUUID().toString());
            statement.setString(2, venue.getName());


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
                    "Facilities = ? WHERE VenueUUID = ?");
            statement.setString(1, venue.getName());
            statement.setString(2, venue.getJsonFacilities());

            statement.setString(3, venue.getUUID().toString());

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
        System.out.println(query);
        try(Statement statement = database.getCon().createStatement()){
            ResultSet rs = statement.executeQuery(query);

            List<Venue> venues = new ArrayList<>();

            while (rs.next()) {
                venues.add(new Venue(rs.getString(1), rs.getString(2),
                        rs.getString(3)));
            }
            return Optional.of(venues);

        }catch(Exception e){
            database.close();
            System.out.println("\nVenue Dao: Search");
            System.out.println(e);
            return Optional.empty();

        }
    }
}