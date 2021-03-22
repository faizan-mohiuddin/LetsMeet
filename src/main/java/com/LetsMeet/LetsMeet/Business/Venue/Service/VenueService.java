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
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

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

    public Object[] createVenue(User user, String name, String businessUUID){
        // Returns [String, Venue]
        Object[] arr = new Object[2];
        arr[1] = null;

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
                        arr[0] = "Venue created successfully";
                        arr[1] = venue;
                        return arr;
                    }
                }

                arr[0] = "Error creating venue";
                return arr;
            }
            arr[0] = "You do not have permission to create a Venue for this Business";
            return arr;
        }
        arr[0] = "Invalid businessID, cannot create Venue";
        return arr;
    }

    public Object[] createVenue(User user, String name, String businessUUID, List<String> facilities, String location,
                                String latitude, String longitude){
        // Returns [String, Venue]
        Object[] arr = new Object[2];
        arr[1] = null;

        // Check business exists
        Business business = businessService.getBusiness(businessUUID);

        if(business != null) {
            // Check user has permission to make venue on behalf of business
            if (businessService.isOwner(user, business)) {

                // Generate UUID
                UUID uuid = this.generateUUID(name, user);

                // Create internal venue object
                Venue venue = new Venue(uuid, name, facilities, location, longitude, latitude);

                // Save in DB
                if (DAO.save(venue)) {

                    // Create internal businessHasVenue object
                    VenueBusiness owner = new VenueBusiness(business.getUUID(), uuid);
                    if (venueBusinessDAO.save(owner)) {
                        arr[0] = "Venue created successfully";
                        arr[1] = venue;
                        return arr;
                    }
                }

                arr[0] = "Error creating venue";
                return arr;
            }
            arr[0] = "You do not have permission to create a Venue for this Business";
            return arr;
        }
        arr[0] = "Invalid businessID, cannot create Venue";
        return arr;
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

    public String deleteVenue(User user, Venue venue){
        return this.deleteVenue(user, venue.getUUID().toString());
    }

    public Venue getVenue(String venueUUID){
        Optional<Venue> venue = DAO.get(UUID.fromString(venueUUID));
        if(venue.isPresent()){
            return venue.get();
        }
        return null;
    }

    public String updateVenue(Venue venue){
        if(DAO.update(venue)){
            return "Venue successfully updated";
        }
        return "Error updating venue";
    }

    public String updateVenue(Venue venue, String name, String facilities, String venueLocation, String venueLatitude,
                              String venueLongitude){
        venue.setName(name);
        venue.setFacilities(facilities);
        venue.setLocation(venueLocation, venueLatitude, venueLongitude);
        return this.updateVenue(venue);
    }

    public String addFacility(Venue venue, String facility){
        venue.addFacility(facility);
        if(DAO.update(venue)){
            return "Facility added to Venue successfully";
        }
        return "Error adding facility to Venue";
    }

    public boolean checkUserPermission(Venue venue, User user){
        // Check if user has permission to work on venue
        this.findBusiness(venue);
        return businessService.isOwner(user, venue.getBusiness());
    }

    public void findBusiness(Venue venue){
        // Set venue.business to a business object
        Optional<VenueBusiness> record = venueBusinessDAO.get(venue.getUUID());
        if(record.isPresent()){
            venue.setBusiness(businessService.getBusiness(record.get().getBusinessUUID()));
        }
    }

    public List<Venue> search(String name, String unparsedFacilitiesList){
        // If neither name nor unparsedFacilitiesList have anything to search for, return all
        if(name.length() == 0 && unparsedFacilitiesList.equals("")){
            Collection<Venue> venues = DAO.getAll().get();
            List<Venue> v = new ArrayList<>(venues);
            return v;
        }

        // Build a query to execute
        String query = String.format("SELECT * FROM Venue WHERE ");

        boolean nameSearch = false;
        if(name.length() > 0){
            //query = query + String.format("Venue.Name = '%s'", name);
            query = query + "Venue.Name LIKE '%" + String.format("%s", name) + "%'";
            //query = query + String.format("MATCH(Venue.Name) AGAINST('*%s*')", name);
            nameSearch = true;
        }

        // Parse facilities list
        if(!unparsedFacilitiesList.equals("")) {
            try {
                JSONArray parsedFacilitiesList = new JSONArray(unparsedFacilitiesList);

                if (nameSearch) {
                    query = query + String.format(" AND ");
                }

                int len = parsedFacilitiesList.length();

                for (int i = 0; i < len; i++) {
                    query = query + "Venue.Facilities LIKE '%" + String.format("%s", parsedFacilitiesList.get(i)) + "%'";

                    if(i + 1 < len){
                        query = query + String.format(" AND ");
                    }
                }
            } catch (Exception e) {
                System.out.println("VenueService.Search");
                System.out.println(e);
            }
        }

        // Check ending of query
        if(query.endsWith(" AND ")){
            query = query.substring(0, query.length() - 6);
        }

        // Query DB
        Optional<List<Venue>> venues = DAO.search(query);
        if(venues.isPresent()){
            return venues.get();
        }
        return null;
    }

    public List<Venue> searchByRadius(double longitude, double latitude, double kilometers){
        Optional<List<Venue>> response = DAO.searchByRadius(longitude, latitude, kilometers);
        if(response.isPresent()){
            return response.get();
        }
        return null;
    }

    public void removeFacility(Venue venue, String facility){
        // Remove facility from venue if its there
        int len1 = venue.numFacilities();
        venue.removeFacility(facility);
        int len2 = venue.numFacilities();
        if(len2 < len1){
            updateVenue(venue);
        }
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
