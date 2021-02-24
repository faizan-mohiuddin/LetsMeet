package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
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
    @Autowired
    DBConnector database;

    @Override
    public Optional<VenueBusiness> get(UUID uuid1, UUID uuid2) {
        return Optional.empty();
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
