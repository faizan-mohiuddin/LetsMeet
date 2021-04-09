package com.LetsMeet.LetsMeet.Event.Controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Events;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Polls;
import com.LetsMeet.LetsMeet.Event.Service.EventCoordinator;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/v2")
@SessionAttributes("userlogin")
public class EventsControllerWeb {

    private static final Logger LOGGER=LoggerFactory.getLogger(EventsControllerWeb.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired 
    private MediaService mediaService;

    @Autowired 
    private PollService pollService;

    @Autowired
    private EventCoordinator eventCoordinator;

    private static String EVENT_NEW = "event/new";
    private static String EVENT = "event/event";
    private static String EVENT_EDIT = "";

    private static String USER_ATTR = "user";

    /**
     * Serves a page for creating new events
     * @param model
     * @param session
     * @param redirectAttributes
     * @return
     */
    @GetMapping({"/createevent", "/event/new"})
    public ModelAndView httpEventNewGet(HttpServletRequest request, Model model, HttpSession session, RedirectAttributes redirectAttributes){
        try{
            User user = validateSession(session);
            model.addAttribute(USER_ATTR, user);

            return new ModelAndView(EVENT_NEW, model.asMap(), HttpStatus.OK);
        }
        catch (Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error ocurred");

            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping({"/createevent", "/event/new"})
    public String httpEventNewPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @ModelAttribute EventDTO eventDTO){
            try {
                User user = validateSession(session);
                Event event = Events.from(eventDTO); 

                // Save event
                eventService.save(event, user);

                // Save any Polls
                for (String p : eventDTO.getPolls()){
                    var poll = Polls.fromJson(p);
                    pollService.create(user, poll);
                    eventService.addPoll(event, poll);
                }

                return "redirect:/Home";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/Home";
        }
    }


    @PostMapping("/event/{EventUUID}/edit")
    public ModelAndView httpEventEditPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
    @ModelAttribute EventDTO eventDTO,
    @PathVariable("EventUUID") String eventUUID) {

        try{
            User user = validateSession(session);
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            
            // Update event
            eventService.update(user, event, eventDTO);

            // Update any polls
            List<Poll> polls = eventService.getPolls(event);
            
            for (String p : eventDTO.getPolls()){
                List<Poll> polls = eventService.getPolls(event);
                
                var poll = Polls.fromJson(p);
                pollService.create(user, poll);
                eventService.addPoll(event, poll);
            }
            
            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);

        } catch(Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred");

            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);
        }
        

    }




    private User validateSession(HttpSession session) throws IllegalAccessException{
        User user = (User) session.getAttribute("userlogin");
        if (user == null) 
            throw new IllegalArgumentException("Permission Denied");
        else return user;
    }

}
