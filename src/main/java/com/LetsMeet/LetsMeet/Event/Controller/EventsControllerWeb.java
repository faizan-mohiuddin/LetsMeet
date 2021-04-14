package com.LetsMeet.LetsMeet.Event.Controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Events;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Polls;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/v2")
@SessionAttributes("userlogin")
public class EventsControllerWeb {

    private static final Logger LOGGER=LoggerFactory.getLogger(EventsControllerWeb.class);

    @Autowired
    private EventService eventService;

    @Autowired 
    private PollService pollService;

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss a"; 
    public static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    /**
     * Poll model data interface
     */
    public static class _PollData{
        public String json;
        public String name;
        public Map<String, Integer> options;

        _PollData(Poll poll){
            this.json = poll.toJson();
            this.name = poll.getName();
            this.options = poll.getOptions();
        }
    }

    /**
     * DateTimeRange model data interface
     */
    public static class _DateTimeData{
        public String json;
        public String startDate;
        public String startTime;
        public String endDate;
        public String endTime;

        _DateTimeData(DateTimeRange dateTimeRange){
            this.json = dateTimeRange.toJson();
            this.startDate = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.startTime = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.endDate = LDT_FORMATTER.format(dateTimeRange.getStart());
            this.endDate = LDT_FORMATTER.format(dateTimeRange.getStart());
        }
    }

    private static String EVENT_TEMPLATE_EDITOR = "event/new";
    // TODO impliment event viewing stuff private static String EVENT_TEMPLATE_VIEWER = "event/event"

    private static String USER_ATTR = "user";

    /**
     * Serves a page for creating new events
     * @param model
     * @param session
     * @param redirectAttributes
     * @return
     */
    @GetMapping({"/createevent", "/event/new"})
    public String httpEventNewGet(HttpServletRequest request, Model model, HttpSession session, RedirectAttributes redirectAttributes){
        try{
            User user = validateSession(session);
            model.addAttribute(USER_ATTR, user);

            if (model.getAttribute("event") == null){
                EventDTO eventDTO = dtoFromEvent(new Event(""));
                model.addAttribute("event", eventDTO);
            }

                model.addAttribute("times", null);

                model.addAttribute("polls", null);

                model.addAttribute("title", "New Meet");
                model.addAttribute("icon", "bi-plus-square");
                model.addAttribute("onSubmit", "/v2/event/new");

            return EVENT_TEMPLATE_EDITOR;
        }
        catch (Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error ocurred");

            return "redirect:/Home";
        }
    }

    /**
     * Handles request to create a new event
     * @param eventDTO Must be present with all attributes to correctly initialise event
     * @return The create events page
     */
    @PostMapping({"/createevent", "/event/new"})
    public String httpEventNewPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @ModelAttribute EventDTO eventDTO){
            try {

                eventDTO.validate();

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

                redirectAttributes.addFlashAttribute("success", "Event created!");
                return "redirect:/event/" + event.getUUID().toString();
            } catch (Exception e) {
                LOGGER.error("Could not create new event: {}", e.getMessage());

                if (e.getMessage().contains("Invalid Poll JSON"))
                    redirectAttributes.addFlashAttribute("warning", "There was an issue creating one or more polls.");

                redirectAttributes.addFlashAttribute("danger", "The Event could not be created, please try again later. ");

                // We don't want users to enter all their details again
                redirectAttributes.addFlashAttribute("event", eventDTO);
                
                return "redirect:/v2/createevent";
        }
    }

    @GetMapping("/event/{EventUUID}/edit")
    public String httpEventEditGet(HttpServletRequest request, Model model, HttpSession session, RedirectAttributes redirectAttributes,
    @PathVariable("EventUUID") String eventUUID){
        try{
            User user = validateSession(session);
            model.addAttribute(USER_ATTR, user);

            // Add Event to model
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();

            EventDTO eventDTO = dtoFromEvent(event);
            model.addAttribute("event", eventDTO);

            // Add times to model
            List<_DateTimeData> times = new ArrayList<>();
            for (var v : event.getEventProperties().getTimes()){
                times.add(new _DateTimeData(v));
            }
            model.addAttribute("times", times);

            // Add polls to model
            List<_PollData> polls = new ArrayList<>();
            for (var poll : eventService.getPolls(event)){
                polls.add(new _PollData(poll));
            }
            model.addAttribute("polls", polls);

            // Setup form
            model.addAttribute("title", "Edit Meet");
            model.addAttribute("icon", "bi-pen");
            model.addAttribute("onSubmit", "/v2/event/" + eventUUID + "/edit");

            return EVENT_TEMPLATE_EDITOR;
        }
        catch (Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred");

            return "redirect:/Home";
        }
    }


    @PostMapping("/event/{EventUUID}/edit")
    public String httpEventEditPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
    @ModelAttribute EventDTO eventDTO,
    @PathVariable("EventUUID") String eventUUID) {

        try{
            User user = validateSession(session);
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            
            eventDTO.validate();

            // Update event
            eventService.update(user, event, eventDTO);

            // Update any polls
            List<UUID> newPolls = new ArrayList<>();
            for (String p : eventDTO.getPolls()){
                var newPoll = Polls.fromJson(p);
                newPolls.add(newPoll.getUUID());         
                var existingPoll = pollService.getPoll(newPoll.getUUID());      // Check if poll already exists
                if (existingPoll.isPresent()){                                  // If so, update it
                    pollService.update(newPoll);
                }
            else{                                                               // Otherwise create it
                    pollService.create(user, newPoll);
                    eventService.addPoll(event, newPoll);
                }
            }

            List<Poll> toDelete = eventService.getPolls(event);
            toDelete.removeIf(n -> (newPolls.contains(n.getUUID())));
            for (Poll poll : toDelete){
                eventService.removePoll(event, poll);
                // TODO pollService.delete(poll)
            }
            
            return "redirect:/event/{EventUUID}";

        } catch(Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred");

            return EVENT_TEMPLATE_EDITOR;
        }
        

    }


    private User validateSession(HttpSession session) throws IllegalAccessException{
        User user = (User) session.getAttribute("userlogin");
        if (user == null) 
            throw new IllegalArgumentException("Permission Denied");
        else return user;
    }

    private EventDTO dtoFromEvent(Event event){
        return new EventDTO(
            event.getUUID().toString(),
            event.getName(),
            event.getDescription(), 
            event.getEventProperties().getLocation().getName(), 
            event.getEventProperties().getLocation().getLongitude(), 
            event.getEventProperties().getLocation().getLatitude(),
            event.getEventProperties().getLocation().getRadius(),
            new ArrayList<>(), 
            event.getEventProperties().getFacilities(), 
            new ArrayList<>(), 
            null, 
            new ArrayList<>());
    }

}
