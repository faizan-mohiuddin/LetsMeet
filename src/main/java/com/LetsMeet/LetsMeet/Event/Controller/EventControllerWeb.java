package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Root.Media.Media;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;

import com.LetsMeet.LetsMeet.User.Service.UserServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("userlogin")
public class EventControllerWeb {

    private static final Logger LOGGER=LoggerFactory.getLogger(EventControllerWeb.class);

    @Autowired
    EventService eventService;

    @Autowired
    EventResponseService responseService;

    @Autowired
    UserService userService;

    @Autowired
    MediaService mediaService;

    @Autowired
    EventDao eventDao;

    @Autowired
    UserService userServiceInterface;

    @GetMapping({"/createevent", "/event/new"})
    public String newEvent(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        } 
        
        else {
            model.addAttribute("user", user);
            return "createevent";
        }
    }

    @PostMapping("/event/{EventUUID}/edit")
    public String updateEvent (HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @PathVariable("EventUUID") String eventUUID,
        @RequestParam("file") MultipartFile file, 
        @RequestParam(name = "eventname") String eventname, 
        @RequestParam(name = "eventdesc") String eventdesc, 
        @RequestParam(name = "eventlocation") String eventlocation){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventUUID);
        if (user == null || event == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when editing the event.");
            return "redirect:/Home";
        }
        
        try{
            event.setName(eventname);
            event.setDescription(eventdesc);
            event.setLocation(eventlocation);

            if (file.getSize()>0){
                String path= mediaService.saveMedia(new Media(file, user.getUUID())).orElseThrow();
                eventService.setProperty(event, "header_image", path);
            }

            eventDao.update(event);
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Could not update event. Please try again later.");
            return "redirect:/Home";
        }

        // Delegate to viewEvent to return the modified event's page
        return viewEvent(eventUUID, model, redirectAttributes, session);

    }


    @PostMapping({"/createevent", "/event/new"})
    public String saveEvent(HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @RequestParam("file") MultipartFile file, 
        @RequestParam(name = "eventname") String eventname, 
        @RequestParam(name = "eventdesc") String eventdesc, 
        @RequestParam(name = "eventlocation") String eventlocation, @RequestParam(name = "thelat") String eventlatitude, @RequestParam(name = "thelong") String eventLongitude) {

        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the event.");
            return "redirect:/Home";
        }

        try{

            model.addAttribute("user", user);
            model.addAttribute("eventname", eventname);
            model.addAttribute("eventdesc", eventdesc);
            model.addAttribute("eventlocation", eventlocation);
            
            Event event = eventService.createEvent(eventname, eventdesc, eventlocation, user.getUUID().toString());

            // Store and set header image file if present
            if (file.getSize()>0){
                String path= mediaService.saveMedia(new Media(file, user.getUUID())).orElseThrow();
                eventService.setProperty(event, "header_image", path);
                eventDao.update(event);
            }

            return viewEvent(event.getUUID().toString(), model, redirectAttributes, session);
        }

        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Creation failed");
            return "redirect:/Home";
        }
    }

    @GetMapping("/adminviewallevents")
    public String adminviewallevents(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");
        if (user == null || !user.getIsAdmin()) { // checks if logged in user is an admin
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        } 
        
        else {
            model.addAttribute("user", user);
            model.addAttribute("allevents", eventService.getEvents());
            return "adminviewallevents";
        }
    }

    @GetMapping("/deleteevent/{eventuuid}")
    public String deleteEvent(@PathVariable("eventuuid") String eventUUID, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventUUID);
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to execute this action.");
            return "redirect:/Home";
        } 
        else {
            if (eventService.deleteEvent(event, user)) {
                redirectAttributes.addFlashAttribute("success", "The event was successfully deleted.");
            } 
            
            else {
                redirectAttributes.addFlashAttribute("danger", "An error occurred when deleting the event.");
            }
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/event/{eventuuid}")
    public String viewEvent(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        // Checks if the currently logged in user is IN (not is owner) of an event.

        // Check if user is logged in AND if the event exists AND if the user is in said event
        if (user != null && eventService.getEvent(eventuuid) != null) {
            
            Event event = eventService.getEvent(eventuuid);
            model.addAttribute("user", user);
            model.addAttribute("event", event);

            Boolean hasCurrentUserRespondedToEvent = false;

            if (responseService.getResponse(user, event).isPresent()){hasCurrentUserRespondedToEvent = true;}
            model.addAttribute("hasUserRespondedToEvent", hasCurrentUserRespondedToEvent);

            if (eventService.checkOwner(event.getUUID(), user.getUUID())) {

                model.addAttribute("isOwnerOfEvent", true);

                List<HashMap<String,Object>> responses= new ArrayList<>();
                for (EventResponse o : responseService.getResponses(event)){
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("user", userService.getUserByUUID(o.getUser().toString()));
                    data.put("response", o);
                    responses.add(data);
                }
    
                model.addAttribute("responses", responses);
            }

            return "viewevent";
        }else {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        }

    }

    @PostMapping("/event/{eventUUID}/users")
    public String eventUsers(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(value="usersRequired") List<String> userUUIDs){
        try{
            Event event = eventService.getEvent(eventUUID);
            List<User> users = new ArrayList<>();
            for ( var v : userUUIDs){
                users.add(userService.getUserByUUID(v));
            }
            for (var user : users){
                responseService.createResponse(user, event, false);
            }
            redirectAttributes.addFlashAttribute("success","Invitation sent!");
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("danger","Failed to invite users: " + e.getMessage());
        }
        return "redirect:/event/{eventUUID}";
    }

    @GetMapping("/event/{eventUUID}/results")
    public String eventResults(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "duration", defaultValue = "30") int duration,
        @RequestParam( value = "attendance", defaultValue = "90") int attendance,
        @RequestParam( value = "requiredUsers", defaultValue = "false") boolean requiredUsers) {

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            model.addAttribute("user", user);
            model.addAttribute("event", eventService.getEvent(eventuuid));

            
            model.addAttribute("results",eventService.calculateResults(event, user, duration,requiredUsers));
            
            return "event/results";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }



    }

    @GetMapping("/event/{eventuuid}/respond")
    public String respondEvent(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        // Get and validate user and event
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventuuid}";
        }

        // Get or create response
        EventResponse response = responseService.getResponse(user, event).orElse(responseService.createResponse(user, event, true));

        // Do stuff to the response
        responseService.clearTimes(response);

        redirectAttributes.addFlashAttribute("success", "You have joined the event.");
        return "redirect:/event/{eventuuid}";


    }

    @GetMapping("/event/{eventuuid}/respond2")
    public String respondEvent2(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user != null){
            model.addAttribute("user", user);
            model.addAttribute("event", eventService.getEvent(eventuuid));
        }

        else { 
            LOGGER.error("Response error");
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");return "redirect:/Home";}


        return "event/response";
    }

    @GetMapping("/event/{eventuuid}/edit")
    public String editEvent(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user != null){
            model.addAttribute("user", user);
            model.addAttribute("event", eventService.getEvent(eventuuid));
        }

        else { redirectAttributes.addFlashAttribute("danger", "An error occurred.");return "redirect:/Home";}


        return "event/edit";
    }
}
