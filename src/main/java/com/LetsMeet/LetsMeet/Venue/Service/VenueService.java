package com.LetsMeet.LetsMeet.Venue.Service;


import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import com.LetsMeet.LetsMeet.Venue.Controller.VenueControllerWeb;
import com.LetsMeet.LetsMeet.Venue.DAO.VenueBusinessDAO;
import com.LetsMeet.LetsMeet.Venue.DAO.VenueDAO;
import com.LetsMeet.LetsMeet.Venue.DAO.VenueTimesDAO;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Venue.Model.VenueOpenTimes;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import javax.mail.Header;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Component
public class VenueService {

    private final double p = Math.PI/180;  // Used for calculating distance between 2 sets or longitude and latitude

    private static final Logger LOGGER= LoggerFactory.getLogger(VenueService.class);

    @Autowired
    VenueDAO DAO;

    @Autowired
    VenueBusinessDAO venueBusinessDAO;

    @Autowired
    VenueTimesDAO venueTimesDAO;

    @Autowired
    BusinessDAO businessDAO;

    @Autowired
    BusinessService businessService;

    @Autowired
    LetsMeetConfiguration config;

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
            Venue v = venue.get();
            try {
                Optional<VenueOpenTimes> times = venueTimesDAO.get(v.getUUID());
                times.ifPresent(v::setOpenTimes);
            }catch(Exception e){
                e.printStackTrace();
            }
            return v;
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

    public List<Venue> search(String name, String unparsedFacilitiesList, String location, String longitude, String latitude,
                              String radius, String time, String hours, String minutes, String day){

        // If neither name nor unparsedFacilitiesList have anything to search for, return all
        if(name.length() == 0 && unparsedFacilitiesList.equals("") && location.equals("") && longitude.equals("") &&
                latitude.equals("") && time.equals("") && hours.equals("") && minutes.equals("") && day.equals("")){
            Collection<Venue> venues = DAO.getAll().get();
            List<Venue> v = new ArrayList<>(venues);
            return v;
        }

        // Build a query to execute
        String query = String.format("SELECT DISTINCT Venue.VenueUUID, Venue.Name, Venue.Facilities, Venue.Address, Venue.Longitude, Venue.Latitude " +
                "FROM Venue, VenueOpeningTimes WHERE ");

        boolean nameSearch = false;
        boolean facilitySearch = false;
        boolean locationSearch = false;
        boolean coordSearch = false;
        boolean timeSearch = false;
        boolean durationSearch = false;
        boolean daySearch = false;

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
                    facilitySearch = true;
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

        // Check address
        if(!location.equals("")){
            // Split on spaces
            List<String> parts = Arrays.asList(location.split(" "));

            if(nameSearch || facilitySearch){
                query = query + String.format(" AND ");
            }

            // Iterate over sections of string
            int len = parts.size();
            for(int i = 0; i < len; i++) {
                locationSearch = true;
                query = query + "Venue.Address LIKE '%" + String.format("%s", parts.get(i)) + "%'";
                if(i + 1 < len){
                    query = query + String.format(" AND ");
                }
            }
        }

        // Check for location search
        if(!longitude.equals("") && !latitude.equals("") && !radius.equals("")){
            // Convert parameters to double
            try{
                double dlong = Double.parseDouble(longitude);
                double dlat = Double.parseDouble(latitude);
                double dradius = Double.parseDouble(radius);

                if(nameSearch || facilitySearch || locationSearch){
                    query = query + String.format(" AND ");
                }

                query = query + String.format("12742 * ASIN(SQRT(" +
                                "0.5 - (COS((Venue.Latitude - %f) * %f)/2)" +
                                " + COS(%f * %f) * COS(Venue.Latitude * %f) * (1 - COS((Venue.Longitude - %f) * %f))/2)) <= %f",
                        dlat, this.p, dlat, this.p, this.p, dlong, this.p, dradius);
                coordSearch = true;
            }catch (Exception e){
                // We dont care, carry on with the rest of the search
                if(!(locationSearch || nameSearch || facilitySearch)){
                    Collection<Venue> venues = DAO.getAll().get();
                    List<Venue> v = new ArrayList<>(venues);
                    return v;
                }
            }
        }

        // Check for time
        if(!time.equals("")){
            if(nameSearch || facilitySearch || locationSearch || coordSearch){
                query = query + String.format(" AND ");
            }

            query = query + String.format("Venue.VenueUUID = VenueOpeningTimes.VenueUUID AND '%s' >= VenueOpeningTimes.openHour " +
                    "AND '%s' <= VenueOpeningTimes.closeHour", time, time);
            timeSearch = true;
        }

        // Check for duration
        if((!hours.equals("") || !minutes.equals("")) && timeSearch){
            query = query + String.format(" AND ");

            // Given time + duration <= venueOpeningTimes.closeHour
            String[] parts = time.split(":");
            int givenHours = Integer.parseInt(parts[0]);
            int givenMinutes = Integer.parseInt(parts[1]);

            int durationHours;
            int durationMinutes;
            try{
                durationHours = Integer.parseInt(hours);
            }catch(Exception e){
                durationHours = 0;
            }

            try{
                durationMinutes = Integer.parseInt(minutes);
            }catch(Exception e){
                durationMinutes = 0;
            }

            givenMinutes = givenMinutes + durationMinutes;
            if (givenMinutes >= 60){
                givenHours += 1;
                givenMinutes -= 60;
            }

            givenHours = (givenHours + durationHours) % 24;

            // Reconstruct String
            String endTime = String.format("%d:%d", givenHours, givenMinutes);

            query = query + String.format("VenueOpeningTimes.closeHour >= '%s'", endTime);
            durationSearch = true;
        }

        // Check for day
        if(!day.equals("") && timeSearch){
            // Check day is more than 0 and less than 8
            int d = Integer.parseInt(day);
            if(d > 0 && d < 8) {
                query = query + String.format(" AND ");

                query = query + String.format("VenueOpeningTimes.DayOfWeek = '%s'", day);
                daySearch = true;
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

    public List<Venue> search(String term){
        String likeTerm = "'%" + String.format("%s", term) + "%'";
        String query = String.format("SELECT * FROM Venue WHERE Venue.VenueUUID = '%s' OR " +
                "Venue.Name LIKE %s OR Venue.Facilities LIKE %s OR Venue.Address LIKE %s",
                term, likeTerm, likeTerm, likeTerm);

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

    public List<Venue> searchWithDate(String name, String unparsedFacilitiesList, String location, String longitude, String latitude,
                              String radius, String time, String hours, String minutes, String date){
        date = date.replaceAll(":", "-");
        return this.search(name, unparsedFacilitiesList, location, longitude, latitude, radius, time, hours, minutes,
                String.valueOf(venueTimesDAO.getDayFromDate(date)));
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

    public void saveVenueTimes(Venue venue){
        try {
            venueTimesDAO.save(venue.getOpenTimes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String formatFacilitiesForSearch(String f){
        String facilities = "[";
        List<String> facs = Arrays.asList(f.split(","));
        for(String fac : facs){
            facilities = facilities + "\"" + fac + "\"";
        }
        facilities = facilities + "]";
        return facilities;
    }

    // Methods for external data
    public List<Venue> externalVenueSearch(double longitude, double latitude, double kilometers){
        String googleDataAPIAddress = "https://maps.googleapis.com/maps/api/place/findplacefromtext/output?parameters";

        // Check input


        // Form url
        try {
            // Required parameters
            int radius = (int) Math.round(kilometers * 1000);

            String requestUrl = String.format("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=%s" +
                    "&location=%s&radius=%d", config.getGmapsKey(), String.format("%f,%f", latitude, longitude), radius);

            Request request = new Request.Builder()
                    .url(requestUrl)
//                    .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
//                    .addHeader("key", config.getGmapsKey())
//                    .addHeader("location", String.format("%f,%f", latitude, longitude))
//                    .addHeader("radius", String.format("%d", radius))
//                    .addHeader("language", "en-GB")
                    .build();

            System.out.println(config.getGmapsKey());

            LOGGER.info("Google request: " + request.toString());
            System.out.println(request.headers());

            // Send request
            OkHttpClient httpClient = new OkHttpClient();

            Response response = httpClient.newCall(request).execute();
            
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());

        }catch(Exception e){
            LOGGER.error("Error sending request: {}", e.getMessage());
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
