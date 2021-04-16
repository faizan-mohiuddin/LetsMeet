package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.Model.DTO.DTO;
import com.LetsMeet.LetsMeet.Event.Model.DTO.ResponseDTO;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(("/event/{eventUUID}/respond"))
@SessionAttributes("userlogin")
public class ResponsesControllerWeb {

    private static final Logger LOGGER= LoggerFactory.getLogger(ResponsesControllerWeb.class);

    // Dependencies
    private final EventResponseService responseService;
    private final EventService eventService;
    private final UserService userService;

    // Templates
    static final String RESPONSE_TEMPLATE = "event/response";
    static final String GUEST_TEMPLATE = "event/guestResponsePreface";
    static final String SUCCESS_TEMPLATE = "redirect:/event/{eventUUID}";
    static final String ERROR_TEMPLATE = "redirect:/Home";

    public ResponsesControllerWeb(EventResponseService responseService, EventService eventService, UserService userService) {
        this.responseService = responseService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping()
    public String httpResponseGet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(name = "guest", defaultValue = "false") String guest){
        try{
            boolean isGuest = guest.equals("false");

            // Validate and get user
            User user = (isGuest)? validateSession(session) : userService.getUserByUUID(guest);
            model.addAttribute("user", user);

            // Validate and get event
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            model.addAttribute("event",event);

            // Add event dates to model as Json structure - this is a bit hacky
            //StringBuilder eventDateJson = new StringBuilder("[");
            List<String> eventDates = new ArrayList<>();
            for (var date : event.getEventProperties().getTimes())
                eventDates.add(date.toJson());
                //eventDateJson.append(date.toJson()).append(",");
            //eventDateJson.replace(eventDateJson.lastIndexOf(","),eventDateJson.length(),"]");
            model.addAttribute("eventDates",eventDates);

            // Add polls
            List<DTO.PollData> polls = new ArrayList<>();
            for (var poll: eventService.getPolls(event))
                polls.add(new DTO.PollData(poll));

            model.addAttribute("polls",polls);

            // Get or generate temporary response
            EventResponse response = responseService.getResponse(user, event).orElseGet(() -> new EventResponse(user.getUUID(),event.getUUID()));
            ResponseDTO responseDTO = ResponseDTO.fromResponse(response);
            model.addAttribute("response", responseDTO);

            // Setup page
            model.addAttribute("title", "Respond to " + event.getName());
            model.addAttribute("icon", "bi-calendar-check");
            model.addAttribute("onSubmit", "/event/" + eventUUID + "/response");

            // Serve response page
            return RESPONSE_TEMPLATE;
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("warning", "Error:");
            LOGGER.error("Failed to serve response: {}", e.getMessage());
            return ERROR_TEMPLATE;
        }
    }

    @PostMapping()
    public String httpResponseSet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(name = "guest", defaultValue = "false") String guest,
    @ModelAttribute ResponseDTO responseDTO){
        try{
            boolean isGuest = guest.equals("false");

            // Validate and get user
            User user = (isGuest)? validateSession(session) : userService.getUserByUUID(guest);
            model.addAttribute(user);

            // Validate and get event
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            model.addAttribute(event);

            // Validate DTO
            responseDTO.validate();

            // Get Event or create new
            EventResponse response = responseService.getResponse(user,event).orElseGet(() -> responseService.createResponse(user,event,false));
            responseService.update(response,responseDTO);

            // If they are a guest, send them to the guest signup page on completion
            return (isGuest) ? GUEST_TEMPLATE : SUCCESS_TEMPLATE;

        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("warning", "Error:");
            LOGGER.error("Failed to serve response: {}", e.getMessage());
            return RESPONSE_TEMPLATE;
        }
    }

    private User validateSession(HttpSession session){
        User user = (User) session.getAttribute("userlogin");
        if (user == null)
            throw new IllegalArgumentException("Permission Denied");
        else return user;
    }
}
