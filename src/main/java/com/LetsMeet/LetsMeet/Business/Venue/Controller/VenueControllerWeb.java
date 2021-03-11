package com.LetsMeet.LetsMeet.Business.Venue.Controller;

import com.LetsMeet.LetsMeet.Business.Controller.BusinessControllerWeb;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
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

@Controller
@SessionAttributes("userlogin")
public class VenueControllerWeb {
    private static final Logger LOGGER= LoggerFactory.getLogger(VenueControllerWeb.class);

    @Autowired
    VenueService venueService;

    @Autowired
    BusinessService businessService;

    @GetMapping({"/createVenue", "{BusinessID}/Venue/new"})
    public String newEvent(Model model, HttpSession session, RedirectAttributes redirectAttributes,
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
            return "Venue/createVenue";
        }
    }

    @GetMapping("/venue/{VenueID}")
    public String getVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                           @PathVariable(value="VenueID") String venueUUID){
        // Check if user has editing/deleting permissions
        return "Venue/Venue";
    }

    @PostMapping("/venue/new")
    public String saveVenue(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                            @RequestParam(value="Name") String name, @RequestParam(value="businessID") String businessUUID){
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

            // Get business
            //Business b = businessService.getBusiness();

            //Venue v = venueService.createVenue(user, name, b.getUUID().toString());
            //String redirectAddress = String.format("redirect:/Venue/%s", v.getUUID().toString());
            //return redirectAddress;
            return "redirect:/Home";
        }

        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Creation failed");
            return "redirect:/Home";
        }
    }
}
