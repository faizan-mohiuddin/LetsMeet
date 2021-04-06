package com.LetsMeet.LetsMeet.Venue.Controller;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Model.VenueOpenTimes;
import com.LetsMeet.LetsMeet.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.Root.Media.Media;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("userlogin")
public class VenueControllerWeb {
    private static final Logger LOGGER= LoggerFactory.getLogger(VenueControllerWeb.class);

    @Autowired
    VenueService venueService;

    @Autowired
    BusinessService businessService;

    @Autowired
    MediaService mediaService;

    @Autowired
    LetsMeetConfiguration config;

    @GetMapping("{BusinessID}/Venue/new")
    public String newVenue(Model model, HttpSession session, RedirectAttributes redirectAttributes,
                           @PathVariable(value="BusinessID") String businessUUID) {
        User user = (User) session.getAttribute("userlogin");
        Business business = businessService.getBusiness(businessUUID);

        if (user == null) {
            // Check user is logged in
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        }else if(business == null){
            // Check business exists
            redirectAttributes.addFlashAttribute("accessDenied", "This page does not exist.");
            return "redirect:/Home";
        }
        else if(!businessService.isOwner(user, business)) {
            // Check user has permission to create event for this business
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        }else {
            model.addAttribute("user", user);
            model.addAttribute("business", business);

            List<String> strings = new ArrayList<>();
            strings.add(String.format("/%s/Venue/new", business.getUUID().toString())); // Link to creating venue page

            model.addAttribute("strings", strings);
            return "Venue/createVenue";
        }
    }

    @GetMapping("/Venue/{VenueID}")
    public String getVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                           @PathVariable(value="VenueID") String venueUUID){

        // Get venue
        Venue venue = venueService.getVenue(venueUUID);
        venueService.findBusiness(venue);   // Venue.business
        model.addAttribute("venue", venue);

        if(venue.numFacilities() == 0){
            model.addAttribute("facilities", false);
        }else{
            model.addAttribute("facilities", true);
        }

        VenueOpenTimes openTimes = venue.getOpenTimes();
        if(openTimes.numTimes() == 0){
            model.addAttribute("openTimes", null);
        }else{
            List<List<String>> times = openTimes.getTimesWithDays();
            model.addAttribute("openTimes", times);
        }

        LOGGER.info("Venue loading coordinates: " + venue.getLatitude() + ", " + venue.getLongitude());

        // Get user
        User user = (User) session.getAttribute("userlogin");
        if(user == null){
            model.addAttribute("permission", false);
        }else {
            model.addAttribute("user", user);

            // Check if user has editing/deleting permissions
            boolean permission = venueService.checkUserPermission(venue, user);
            model.addAttribute("permission", permission);
        }

        // Get images
        String directoryPath = String.format("%s\\files\\venue\\%s", config.getdataFolder(), venueUUID);
        Path path = Paths.get(directoryPath);

        if(Files.exists(path)){
            // Get image names
            File dir = new File(directoryPath);
            String[] imageNames = dir.list();
            List<List<String>> images = new ArrayList<>();

            String displayPath = String.format("\\media\\files\\venue\\%s", venueUUID);

            int counter = 1;
            for(String s : imageNames){
                List<String> each = new ArrayList<>();
                String t = String.format("%d", counter);
                each.add(t);
                each.add(displayPath + "\\" + s);
                images.add(each);
                counter += 1;
            }
            model.addAttribute("images", true);
            model.addAttribute("firstImage", images.get(0));
            images.remove(0);
            model.addAttribute("Images", images);
        }

        return "Venue/Venue";
    }

    @PostMapping("/venue/new")
    public String saveVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                            @RequestParam(value="Name") String name, @RequestParam(value="businessID") String businessUUID,
                            @RequestParam(value="facilities") String facilities,
                            @RequestParam(value = "venuelocation") String venueLocation,
                            @RequestParam(value = "thelat") String venueLatitude,
                            @RequestParam(value = "thelong") String venueLongitude,
                            @RequestParam(value="TimeRanges") String timeRanges){
        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the Venue.");
            return "redirect:/Home";
        }

        try{

            model.addAttribute("user", user);
            model.addAttribute("venueName", name);

            List<String> facs = new ArrayList<>(Arrays.asList(facilities.split(",")));

            // Get business
            Business b = businessService.getBusiness(businessUUID);

            Object[] response = venueService.createVenue(user, name, b.getUUID().toString(), facs, venueLocation, venueLatitude, venueLongitude);
            Venue v = (Venue) response[1];

            // Set venue Times ranges
            VenueOpenTimes times = v.getOpenTimes();
            times.setTimes(timeRanges);
            v.setOpenTimes(times);
            venueService.saveVenueTimes(v);

            String redirectAddress = String.format("redirect:/Venue/%s", v.getUUID().toString());
            return redirectAddress;
        }

        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Creation failed");
            e.printStackTrace();
            return "redirect:/Home";
        }
    }

    @GetMapping("/Venues")
    public String getAllVenues(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                               @RequestParam(value="VenueName", defaultValue = "") String searchName,
                               @RequestParam(value="Facilities", defaultValue = "") String searchFacilities,
                               @RequestParam(value="location", defaultValue = "") String searchLocation,
                               @RequestParam(value="longitdue", defaultValue = "") String longitude,
                               @RequestParam(value="latitude", defaultValue = "") String latitude,
                               @RequestParam(value="radius", defaultValue = "") String radius){

        User user = (User) session.getAttribute("userlogin");
        model.addAttribute("user", user);

        // searchFacilities should be within square brackets
        if(searchFacilities.length() > 0){
            searchFacilities = "[\"" + searchFacilities + "\"]";
        }

        // Search for events by what is given
        List<Venue> venues = venueService.search(searchName, searchFacilities, searchLocation, longitude, latitude, radius);
        model.addAttribute("venues", venues);

        return "Venue/allVenues";
    }

    @PostMapping("/Venue/{VenueID}/facility/{Facility}")
    public String removeFacility(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                 @PathVariable(value="VenueID") String venueUUID,
                                 @PathVariable(value="Facility") String facilityToRemove){
        System.out.println(venueUUID);
        System.out.println(facilityToRemove);

        // Check user has permission
        User user = (User) session.getAttribute("userlogin");
        Venue venue = venueService.getVenue(venueUUID);

        System.out.println("got venue");

        if(venueService.checkUserPermission(venue, user)){
            // Remove facility from venue
            venueService.removeFacility(venue, facilityToRemove);

            // Redirect to venue page
            System.out.println("reloading");
            String destination = String.format("redirect:/Venue/%s", venueUUID);
            return destination;
        }

        redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to do this.");
        return "redirect:/Home";
    }

    @GetMapping("/Venue/{VenueID}/edit")
    public String editVenuePage(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                            @PathVariable(value="VenueID") String venueUUID){
        // Check user has permission to view the page
        Venue venue = venueService.getVenue(venueUUID);
        if(!(venue == null)) {
            User user = (User) session.getAttribute("userlogin");
            model.addAttribute("user", user);

            if(user == null){
                redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to view this page.");
                return "redirect:/Home";
            }

            if(venueService.checkUserPermission(venue, user)) {
                model.addAttribute("venue", venue);
                List<List<String>> response = venue.getOpenTimes().getTimesWithIndex();
                model.addAttribute("times", response);
                model.addAttribute("numTimes", response.size());
                return "Venue/editVenue";
            }
        }
        redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to view this page.");
        return "redirect:/Home";
    }

    @PostMapping("/Venue/{VenueID}/edit")
    public String editVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                            @PathVariable(value="VenueID") String venueUUID,
                            @RequestParam(value="venueName") String name,
                            @RequestParam(value="facilities", defaultValue = "") String facilities,
                            @RequestParam(value = "venuelocation", defaultValue = "") String venueLocation,
                            @RequestParam(value = "thelat", defaultValue = "") String venueLatitude,
                            @RequestParam(value = "thelong", defaultValue = "") String venueLongitude,
                            @RequestParam(value="TimeRanges") String OpenTimes){
        // Get venue
        Venue venue = venueService.getVenue(venueUUID);

        if(!(venue == null)) {
            // Get user
            User user = (User) session.getAttribute("userlogin");

            // Check if user has permission
            if(venueService.checkUserPermission(venue, user)) {
                // Update venue
                venueService.updateVenue(venue, name, facilities, venueLocation, venueLatitude, venueLongitude);

                //Update venueTimes
                VenueOpenTimes times = venue.getOpenTimes();
                times.setTimes(OpenTimes);
                venue.setOpenTimes(times);
                venueService.saveVenueTimes(venue);

                // Redirect to venue page
                String destination = String.format("redirect:/Venue/%s", venueUUID);
                return destination;
            }
        }

        return "redirect:/Home";
    }

    @GetMapping("/Venue/{VenueID}/delete")
    public String deleteVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                              @PathVariable(value="VenueID") String venueUUID){
        // Check user has permission
        User user = (User) session.getAttribute("userlogin");
        if(user == null){
            redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to carry out this action.");
            return"redirect:/Home";
        }

        Venue venue = venueService.getVenue(venueUUID);
        if(venue == null){
            redirectAttributes.addFlashAttribute("accessDenied", "Invalid action");
            return"redirect:/Home";
        }

        if(venueService.checkUserPermission(venue, user)){
            venueService.findBusiness(venue);
            String destination = String.format("redirect:/Business/%s", venue.business.getUUID().toString());

            // Delete venue
            venueService.deleteVenue(user, venue);

            // Redirect to business page
            return destination;
        }
        redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to carry out this action.");
        return"redirect:/Home";
    }

    @GetMapping("/Venue/{VenueID}/Images")
    public String venueImages(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                              @PathVariable(value="VenueID") String venueUUID){
        // Get venue
        Venue venue = venueService.getVenue(venueUUID);

        // Get user
        User user = (User) session.getAttribute("userlogin");

        if(!(venue == null) && !(user == null)) {
            // Make sure user has permission
            if(venueService.checkUserPermission(venue, user)){
                model.addAttribute("user", user);
                model.addAttribute("venue", venue);
                return "Venue/venueImages";
            }
            redirectAttributes.addFlashAttribute("accessDenied", "You dont have permission to view this page.");
            return "redirect:/Home";
        }

        redirectAttributes.addFlashAttribute("accessDenied", "Something went wrong.");
        return "redirect:/Home";
    }

    @PostMapping("/Venue/{VenueID}/Images")
    public String venueSetImages(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                 @PathVariable(value="VenueID") String venueUUID,
                                 @RequestParam(value="image") MultipartFile[] files){

        List<MultipartFile> images = Arrays.asList(files);
        boolean errorOccured = false;

        for(MultipartFile i : images){

            if (i.getSize()>0) {
                Optional<Media> image = mediaService.newMedia(i, "venue", venueUUID);
                System.out.println(mediaService.generateURL(image.get()));
            }
        }

        if(errorOccured){
            redirectAttributes.addFlashAttribute("accessDenied", "There was a problem with all or some of the images!");
        }

        String destination = String.format("redirect:/Venue/%s", venueUUID);
        return destination;
    }
}
