package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

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

            Optional<Venue> response = Optional.of(new Venue(rs.getString(1), rs.getString(2)));
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
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Venue (VenueUUID, Name) VALUES (?,?)")){
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
        return null;
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
}