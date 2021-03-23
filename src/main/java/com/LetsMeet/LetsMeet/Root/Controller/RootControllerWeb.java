package com.LetsMeet.LetsMeet.Root.Controller;

import com.LetsMeet.LetsMeet.Business.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Business.Venue.Service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;

@Controller
@SessionAttributes("userlogin")
public class RootControllerWeb {

    @Autowired
    EventService eventService;

    @Autowired
    VenueService venueService;


    @GetMapping("/search")
    public String siteSearch(Model model, HttpSession session, RedirectAttributes redirectAttributes, @RequestParam(name = "term")String searchTerm){
        HashMap<String,String> results = new HashMap<>();
        
        User user = (User) session.getAttribute("userlogin");
        model.addAttribute("user", user);

        // Search events
        List<Event> events = eventService.search(searchTerm);
        model.addAttribute("events", events);

        // Search venues
        List<Venue> venues = venueService.search(searchTerm);
        model.addAttribute("venues", venues);

        return "root/searchResults";
    }
}
