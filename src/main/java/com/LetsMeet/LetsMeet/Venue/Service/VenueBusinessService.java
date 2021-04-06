package com.LetsMeet.LetsMeet.Venue.Service;

import com.LetsMeet.LetsMeet.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Model.VenueBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public List<Venue> getBusinessVenues(String businessUUID){
        Optional<Collection<VenueBusiness>> response = DAO.getBusinessVenues(businessUUID);
        if(response.isPresent()) {
            Object[] records = response.get().toArray();
            List<Venue> venues = new ArrayList<>();
            for (Object o : records) {
                VenueBusiness v = (VenueBusiness) o;
                venues.add(venueDAO.get(v.getVenueUUID()).get());
            }
            if(venues.size() > 0) {
                return venues;
            }
        }
        return null;
    }
}
