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

        System.out.println("\nVenue Loading:");
        System.out.println(venue.getLatitude());
        System.out.println(venue.getLongitude());

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
                               @RequestParam(value="Facilities", defaultValue = "") String searchFacilities){
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
}
