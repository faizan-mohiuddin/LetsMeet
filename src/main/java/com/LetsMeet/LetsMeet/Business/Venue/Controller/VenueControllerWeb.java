package com.LetsMeet.LetsMeet.Business.Venue.Controller;

import com.LetsMeet.LetsMeet.Business.Controller.BusinessControllerWeb;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueBusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@SessionAttributes("userlogin")
public class VenueControllerWeb {
    private static final Logger LOGGER= LoggerFactory.getLogger(VenueControllerWeb.class);

    @Autowired
    VenueService venueService;

    @Autowired
    BusinessService businessService;

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
        return "Venue/Venue";
    }

    @PostMapping("/venue/new")
    public String saveVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                            @RequestParam(value="Name") String name, @RequestParam(value="businessID") String businessUUID,
                            @RequestParam(value="facilities") String facilities,
                            @RequestParam(value = "venuelocation") String venueLocation,
                            @RequestParam(value = "thelat") String venueLatitude,
                            @RequestParam(value = "thelong") String venueLongitude){
        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the Venue.");
            return "redirect:/Home";
        }

        try{

            model.addAttribute("user", user);
            model.addAttribute("venueName", name);

            System.out.println("Venue name");
            System.out.println(name);
            System.out.println(businessUUID);
            System.out.println(facilities);
            System.out.println(venueLocation);
            System.out.println(venueLatitude);
            System.out.println(venueLongitude);

            List<String> facs = new ArrayList<>(Arrays.asList(facilities.split(",")));

            // Get business
            Business b = businessService.getBusiness(businessUUID);

            Object[] response = venueService.createVenue(user, name, b.getUUID().toString(), facs, venueLocation, venueLatitude, venueLongitude);
            Venue v = (Venue) response[1];
            String redirectAddress = String.format("redirect:/Venue/%s", v.getUUID().toString());
            return redirectAddress;
        }

        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Creation failed");
            return "redirect:/Home";
        }
    }

    @GetMapping("/Venues")
    public String getAllVenues(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                               @RequestParam(value="VenueName", defaultValue = "") String searchName,
                               @RequestParam(value="Facilities", defaultValue = "") String searchFacilities,
                               @RequestParam(value="location", defaultValue = "") String searchLocation){

        User user = (User) session.getAttribute("userlogin");
        model.addAttribute("user", user);

        // searchFacilities should be within square brackets
        if(searchFacilities.length() > 0){
            searchFacilities = "[" + searchFacilities + "]";
        }

        // Search for events by what is given
        List<Venue> venues = venueService.search(searchName, searchFacilities);
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
                            @RequestParam(value="facilities") String facilities,
                            @RequestParam(value = "venuelocation", defaultValue = "") String venueLocation,
                            @RequestParam(value = "thelat", defaultValue = "") String venueLatitude,
                            @RequestParam(value = "thelong", defaultValue = "") String venueLongitude){
        // Get venue
        Venue venue = venueService.getVenue(venueUUID);

        System.out.println(venueLatitude);
        System.out.println(venueLongitude);

        if(!(venue == null)) {
            // Get user
            User user = (User) session.getAttribute("userlogin");

            // Check if user has permission
            if(venueService.checkUserPermission(venue, user)) {
                // Update venue
                venueService.updateVenue(venue, name, facilities, venueLocation, venueLatitude, venueLongitude);
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
}
