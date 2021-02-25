package com.LetsMeet.LetsMeet.Business.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
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
import java.util.*;

@Component
public class BusinessOwnerDAO implements DAOconjugate<BusinessOwner> {
    // Get logger
    private static final Logger LOGGER= LoggerFactory.getLogger(BusinessOwnerDAO.class);

    @Autowired
    DBConnector database;

    @Override
    public Optional<BusinessOwner> get(UUID businessUUID, UUID userUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from HasBusiness where HasBusiness.BusinessUUID = '%s' and HasBusiness.UserUUID = '%s'",
                    businessUUID.toString(), userUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<BusinessOwner> response = Optional.of(new BusinessOwner(UUID.fromString(rs.getString(1)),
                    UUID.fromString(rs.getString(2))));
            database.close();
            return response;

        }
        catch(Exception e){
            System.out.println("\nBusiness Owner Dao: get(UUID, UUID)");
            //e.printStackTrace();
            System.out.println(e);
            database.close();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Collection<BusinessOwner>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(BusinessOwner businessOwner) {
        database.open();

        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO HasBusiness (BusinessUUID, UserUUID) VALUES (?,?)")){
            statement.setString(1, businessOwner.getBusinessUUID().toString());
            statement.setString(2, businessOwner.getUserUUID().toString());


            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("BusinessOwner Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean update(BusinessOwner businessOwner) {
        return null;
    }

    @Override
    public Boolean delete(BusinessOwner businessOwner) {
        return this.delete(businessOwner.getBusinessUUID().toString(), businessOwner.getUserUUID().toString());
    }

    @Override
    public Boolean delete(String businessUUID, String userUUID) {
        database.open();

        try(Statement statement = database.con.createStatement()){
            String query = String.format("DELETE FROM HasBusiness WHERE HasBusiness.BusinessUUID = '%s' AND " +
                            "HasBusiness.UserUUID = '%s'", businessUUID, userUUID);
            int rows = statement.executeUpdate(query);
            database.close();

            return (rows > 0)? true : false;

        }catch(Exception e){
            LOGGER.error("Failed to delete BusinessOwner: {} ", e.getMessage());
            database.close();
            return false;
        }
    }

    public Optional<Collection<BusinessOwner>> get(String uuid){
        database.open();
        try{
            Statement statement = database.getCon().createStatement();
            String query = String.format("select * from HasBusiness WHERE HasBusiness.UserUUID = '%s' OR " +
                    "HasBusiness.BusinessUUID = '%s'", uuid, uuid);
            ResultSet rs = statement.executeQuery(query);
            Collection<BusinessOwner> businesses = new ArrayList<>();

            while (rs.next()){
                businesses.add(new BusinessOwner(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2))));
            }
            database.close();
            return Optional.ofNullable(businesses);
        }catch(Exception e){
            System.out.println("\nBusiness Owner Dao: Get(UserUUID)");
            System.out.println(e);
            return Optional.empty();
        }
    }
}
