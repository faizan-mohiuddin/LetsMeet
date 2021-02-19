package com.LetsMeet.LetsMeet.Business.Service;

import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventPermission;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class BusinessService {
    @Autowired
    BusinessDAO DAO;

    @Autowired
    BusinessOwnerDAO ownerDAO;

    public String createBusiness(String name, User user){
        UUID uuid = this.generateUUID(name, user);
        Business business = new Business(uuid, name);

        // Add business to DB
        if(DAO.save(business)) {

            // Add connection between business and user
            BusinessOwner owner = new BusinessOwner(uuid, user.getUUID());
            if(ownerDAO.save(owner)) {
                return "Business created successfully";
            }
        }
        return "Error creating business";
    }

    public String deleteBusiness(String businessUUID, String userUUID){
        // Check user has permission to delete
        Optional<BusinessOwner> response = ownerDAO.get(UUID.fromString(businessUUID), UUID.fromString(userUUID));
        if(response.isPresent()){
            // Delete business
            if(DAO.delete(UUID.fromString(businessUUID))){
                return "Business successfully deleted";
            }else{
                return "Error deleting event";
            }
        }
        return "You dont have permission to delete this event";
    }

    public Collection<Business> getUserBusinesses(String userUUID){
        List<Business> businesses = new ArrayList<>();

        // Get list of users permissions
        Collection<BusinessOwner> businessOwners = ownerDAO.get(userUUID).get();

        // Get each event on permissions list
        for (BusinessOwner b : businessOwners){
            businesses.add(DAO.get(b.getBusinessUUID()).get());
        }
        return businesses;
    }

    // Private methods
    private UUID generateUUID(String name, User user){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + user.getStringUUID() + user.getEmail() + user.getfName() + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

}