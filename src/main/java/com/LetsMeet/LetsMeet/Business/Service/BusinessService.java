package com.LetsMeet.LetsMeet.Business.Service;

import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueBusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
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

    @Autowired
    VenueBusinessService venueBusinessService;

    public Business createBusiness(String name, User user){
        UUID uuid = this.generateUUID(name, user);
        Business business = new Business(uuid, name);

        // Add business to DB
        if(DAO.save(business)) {

            // Add connection between business and user
            BusinessOwner owner = new BusinessOwner(uuid, user.getUUID());
            if(ownerDAO.save(owner))  {
                return business;
            }
        }
        return null;
    }

    public String deleteBusiness(String businessUUID, String userUUID){
        // Check user has permission to delete
        Optional<BusinessOwner> response = ownerDAO.get(UUID.fromString(businessUUID), UUID.fromString(userUUID));
        if(response.isPresent()){
            // Delete venues
            venueBusinessService.deleteBusinessVenues(businessUUID);

            // Delete business
            if(DAO.delete(UUID.fromString(businessUUID))){
                return "Business successfully deleted";
            }else{
                return "Error deleting event";
            }
        }
        return "You dont have permission to delete this event";
    }

    public String removeEmptyBusiness(String businessUUID){
        // Check business is empty
        Optional<Collection<BusinessOwner>> owners = ownerDAO.get(businessUUID);
        if(owners.get().size() == 0) {
            // Delete venues
            venueBusinessService.deleteBusinessVenues(businessUUID);

            // Delete business
            if (DAO.delete(UUID.fromString(businessUUID))) {
                return "Business successfully deleted";
            } else {
                return "Error deleting event";
            }
        }
        return "Business still has Owners";
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

    public String joinBusiness(String businessUUID, String userUUID){
        BusinessOwner bo = new BusinessOwner(businessUUID, userUUID);
        if(ownerDAO.save(bo)){
            return "Successfully joined Business";
        }
        return "Error joining Business";
    }

    public String leaveBusiness(String businessUUID, String userUUID){
        // Remove user from business
        ownerDAO.delete(businessUUID, userUUID);

        // If there are no users left in business, delete business
        Optional<Collection<BusinessOwner>> owners = ownerDAO.get(businessUUID);
        if(owners.get().size() == 0){
            // Delete business
            return this.removeEmptyBusiness(businessUUID);
        }
        return "Successfully left Business";
    }

    public Business getBusiness(String businessUUID){
        Optional<Business> business = DAO.get(UUID.fromString(businessUUID));

        if(business.isPresent()){
            return business.get();
        }
        return null;
    }

    public Business getBusiness(UUID businessUUID){
        return this.getBusiness(businessUUID.toString());
    }

    public List<BusinessOwner> businessOwners(String businessUUID){
        Optional<Collection<BusinessOwner>> records = ownerDAO.get(businessUUID);
        if(records.isPresent()){
            List<BusinessOwner> owners = new ArrayList<>();
            for(BusinessOwner b : records.get()){
                owners.add(b);
            }

            if(owners.size() > 1) {
                return owners;
            }
        }
        return null;
    }

    public boolean isOwner(User user, Business business){
        // Check if the user is an owner of the business
        Optional<BusinessOwner> response = ownerDAO.get(business.getUUID(), user.getUUID());
        return response.isPresent();
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
