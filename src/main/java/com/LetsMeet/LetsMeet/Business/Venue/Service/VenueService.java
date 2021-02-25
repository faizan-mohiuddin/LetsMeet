package com.LetsMeet.LetsMeet.Business.Venue.Service;


import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class VenueService {

    @Autowired
    VenueDAO DAO;

    @Autowired
    VenueBusinessDAO venueBusinessDAO;

    @Autowired
    BusinessDAO businessDAO;

    @Autowired
    BusinessService businessService;

    public String createVenue(User user, String name, String businessUUID){
        // Check business exists
        Business business = businessService.getBusiness(businessUUID);

        if(business != null) {
            // Check user has permission to make venue on behalf of business
            if (businessService.isOwner(user, business)) {

                // Generate UUID
                UUID uuid = this.generateUUID(name, user);

                // Create internal venue object
                Venue venue = new Venue(uuid, name);

                // Save in DB
                if (DAO.save(venue)) {

                    // Create internal businessHasVenue object
                    VenueBusiness owner = new VenueBusiness(business.getUUID(), uuid);
                    if (venueBusinessDAO.save(owner)) {
                        return "Venue created successfully";
                    }
                }
                return "Error creating venue";
            }
            return "You do not have permission to create a Venue for this Business";
        }
        return "Invalid businessID, cannot create Venue";
    }

    public String deleteVenue(User user, String venueUUID){
        // Check user has permission to delete
        // Delete venue
        if(DAO.delete(UUID.fromString(venueUUID))){
            return "Venue successfully deleted";
        }else{
            return "Error deleting event";
        }
        //return "You dont have permission to delete this venue";
    }

    public Venue getVenue(String venueUUID){
        Optional<Venue> venue = DAO.get(UUID.fromString(venueUUID));
        if(venue.isPresent()){
            return venue.get();
        }
        return null;
    }

    // Private methods
    private UUID generateUUID(String name, User user){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + user.getStringUUID() + "Venue" + user.getEmail() + user.getfName() + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }
}
