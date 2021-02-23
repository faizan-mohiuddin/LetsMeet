package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class VenueDAO implements DAO<Venue> {

    @Autowired
    DBConnector database;

    @Override
    public Optional<Venue> get(UUID uuid) {
        return Optional.empty();
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
        return null;
    }

    @Override
    public Boolean delete(UUID uuid) {
        return null;
    }
}
