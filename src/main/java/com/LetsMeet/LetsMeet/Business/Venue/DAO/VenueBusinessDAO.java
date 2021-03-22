package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class VenueBusinessDAO implements DAOconjugate<VenueBusiness> {
    // Get logger
    private static final Logger LOGGER= LoggerFactory.getLogger(EventPermissionDao.class);

    @Autowired
    DBConnector database;

    @Override
    public Optional<VenueBusiness> get(UUID businessUUID, UUID venueUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasVenue where HasVenue.BusinessUUID = '%s' " +
                    "and HasVenue.VenueUUID = '%s'", businessUUID.toString(), venueUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<VenueBusiness> response = Optional.of(new VenueBusiness(UUID.fromString(rs.getString(2)),
                    UUID.fromString(rs.getString(1))));
            database.close();
            return response;
        }
        catch(Exception e){
            LOGGER.error("Failed to get VenueBusiness: {} ", e.getMessage());
            database.close();
            return Optional.empty();
        }
    }

    public Optional<VenueBusiness> get(UUID venueUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasVenue where HasVenue.VenueUUID = '%s'", venueUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<VenueBusiness> response = Optional.of(new VenueBusiness(UUID.fromString(rs.getString(2)),
                    UUID.fromString(rs.getString(1))));
            database.close();
            return response;
        }
        catch(Exception e){
            LOGGER.error("Failed to get VenueBusiness: {} ", e.getMessage());
            database.close();
            return Optional.empty();
        }
    }


    @Override
    public Optional<Collection<VenueBusiness>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(VenueBusiness venueBusiness) {
        database.open();

        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO HasVenue (VenueUUID, BusinessUUID) VALUES (?,?)")){
            statement.setString(1, venueBusiness.getVenueUUID().toString());
            statement.setString(2, venueBusiness.getBusinessUUID().toString());


            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("VenueBusiness Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean update(VenueBusiness venueBusiness) {
        return null;
    }

    @Override
    public Boolean delete(VenueBusiness venueBusiness) {
        return null;
    }

    @Override
    public Boolean delete(String uuid1, String uuid2) {
        return null;
    }

    public Optional<Collection<VenueBusiness>> getBusinessVenues(String businessUUID){
        database.open();
        try{
            Statement statement = database.getCon().createStatement();
            String query = String.format("select * from HasVenue WHERE HasVenue.BusinessUUID = '%s'", businessUUID);
            ResultSet rs = statement.executeQuery(query);
            Collection<VenueBusiness> venues = new ArrayList<>();

            while (rs.next()){
                venues.add(new VenueBusiness(UUID.fromString(rs.getString(2)), UUID.fromString(rs.getString(1))));
            }
            database.close();
            return Optional.ofNullable(venues);
        }catch(Exception e){
            System.out.println("\nVenue Business Dao: Get(businessUUID)");
            System.out.println(e);
            return Optional.empty();
        }
    }
}
