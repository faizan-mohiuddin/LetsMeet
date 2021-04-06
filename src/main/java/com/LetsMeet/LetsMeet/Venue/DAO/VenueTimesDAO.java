package com.LetsMeet.LetsMeet.Venue.DAO;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Model.VenueOpenTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


@Service
public class VenueTimesDAO implements DAO<VenueOpenTimes> {
    @Autowired
    DBConnector database;

    private static final Logger LOGGER= LoggerFactory.getLogger(VenueTimesDAO.class);

    @Override
    public Optional<VenueOpenTimes> get(UUID venueUUID) throws IOException {
        try{
            Statement statement = database.getCon().createStatement();
            String query = String.format("SELECT * FROM VenueOpeningTimes WHERE VenueUUID = '%s'", venueUUID.toString());
            ResultSet rs = statement.executeQuery(query);

            VenueOpenTimes venueOpenTimes = new VenueOpenTimes(venueUUID);
            while(rs.next()) {
                venueOpenTimes.setTime(rs.getString("DayOfWeek"), rs.getString("openHour"),
                        rs.getString("closeHour"));
            }
            return Optional.of(venueOpenTimes);
        }catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<VenueOpenTimes>> getAll() throws IOException {
        return Optional.empty();
    }

    @Override
    public Boolean save(VenueOpenTimes venueOpenTimes) throws IOException {
        // For each day, save times
        for(List<String> t : venueOpenTimes.getTimes()){
            this.saveDay(venueOpenTimes.getUUID(), Integer.parseInt(t.get(0)), t.get(1), t .get(2));
        }
        return null;
    }

    public Boolean saveDay(UUID venueUUID, int day, String open, String close){
        try{
            PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO VenueOpeningTimes (VenueUUID, DayOfWeek, " +
                    "openHour, closeHour) VALUES (?,?,?,?)");
            statement.setString(1, venueUUID.toString());
            statement.setInt(2, day);
            statement.setString(3, open);
            statement.setString(4, close);

            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){

            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean update(VenueOpenTimes venueOpenTimes) throws IOException {
        return null;
    }

    @Override
    public Boolean delete(VenueOpenTimes venueOpenTimes) throws IOException {
        return null;
    }

    @Override
    public Boolean delete(UUID uuid) throws IOException {
        return null;
    }

}
