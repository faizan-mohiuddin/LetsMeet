package com.LetsMeet.LetsMeet.Business.Venue.Service;

import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Business.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class VenueBusinessService {
    @Autowired
    VenueBusinessDAO DAO;

    @Autowired
    VenueDAO venueDAO;

    public boolean deleteBusinessVenues(String businessUUID){
        // Get business venues
        Optional<Collection<VenueBusiness>> response = DAO.getBusinessVenues(businessUUID);
        if(response.isPresent()){
            Object[] venues = response.get().toArray();
            for(Object o : venues){
                VenueBusiness v = (VenueBusiness) o;
                venueDAO.delete(v.getVenueUUID());
            }
        }
        return true;
    }
}
