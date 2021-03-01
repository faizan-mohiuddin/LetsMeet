package com.LetsMeet.LetsMeet.Root.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;

@Controller
@SessionAttributes("userlogin")
public class RootControllerWeb {

    @Autowired
    EventService eventService;


    @GetMapping("/search")
    public String siteSearch(Model model, HttpSession session, RedirectAttributes redirectAttributes, @RequestParam(name = "term")String searchTerm){
        HashMap<String,String> results = new HashMap<>();
        
        User user = (User) session.getAttribute("userlogin");
        model.addAttribute("user", user);

        for (Event e : eventService.getEvents()){
            if (e.getName().contains(searchTerm)){
                results.put(e.getName(), e.getUUID().toString());
            }
        }
        model.addAttribute("results", results);

        if (results.isEmpty())
            model.addAttribute("empty", true);
            redirectAttributes.addFlashAttribute("error", "You do not have permission to view this page.");

        return "root/searchResults";
    }
}
