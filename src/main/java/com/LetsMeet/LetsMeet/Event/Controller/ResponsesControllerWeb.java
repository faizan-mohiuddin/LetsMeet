package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.Model.DTO.DTO;
import com.LetsMeet.LetsMeet.Event.Model.DTO.ResponseDTO;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;
import com.LetsMeet.LetsMeet.Event.Poll.Model.PollResponse;
import com.LetsMeet.LetsMeet.Event.Poll.Model.PollResponses;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    private final PollService pollService;

    // Templates
    static final String RESPONSE_TEMPLATE = "event/response";
    static final String GUEST_TEMPLATE = "event/guestResponsePreface";
    static final String SUCCESS_TEMPLATE = "redirect:/event/{eventUUID}";
    static final String ERROR_TEMPLATE = "redirect:/Home";

    public ResponsesControllerWeb(EventResponseService responseService, EventService eventService, UserService userService, PollService pollService) {
        this.responseService = responseService;
        this.eventService = eventService;
        this.userService = userService;
        this.pollService = pollService;
    }

    @GetMapping()
    public String httpResponseGet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(name = "guest", defaultValue = "false") String guest){
        try{
            boolean isGuest = !guest.equals("false");

            // Validate and get user
            User user = (isGuest)? userService.getUserByUUID(guest) : validateSession(session) ;
            model.addAttribute("user", user);

            // Validate and get event
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            model.addAttribute("event",event);

            // Add event dates to model as Json structure - this is a bit hacky
            List<String> eventDates = new ArrayList<>();
            for (var date : event.getEventProperties().getTimes())
                eventDates.add(date.toJson());
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
            model.addAttribute("onSubmit", "/event/" + eventUUID + "/respond");

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
            boolean isGuest = !guest.equals("false");

            // Validate and get user
            User user = (isGuest)? userService.getUserByUUID(guest) : validateSession(session);
            model.addAttribute(user);

            // Validate and get event
            Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
            model.addAttribute(event);

            // Validate DTO
            responseDTO.validate();

            // Get Event or create new
            EventResponse response = responseService.getResponse(user,event).orElseGet(() -> responseService.createResponse(user,event,false));
            responseService.update(response,responseDTO);

            List<Poll> polls = eventService.getPolls(event);
            List<String> responses = responseDTO.getPollResponse();
            if (polls.size() != responses.size()) throw new IllegalArgumentException("Number of Poll Responses does not match number of Polls");

            for (int i = 0; i < polls.size() ; i++) {
                List<String> selected= new Gson().fromJson(responses.get(i), new TypeToken<List<String>>(){}.getType());
                pollService.addResponse(polls.get(i), selected);
            }

            // If they are a guest, send them to the guest signup page on completion
            return (isGuest) ? GUEST_TEMPLATE : SUCCESS_TEMPLATE;

        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("warning", "Error:");
            LOGGER.error("Failed to serve response: {}", e.getMessage());
            return RESPONSE_TEMPLATE;
        }
    }

    // Error catching
    @ExceptionHandler(Exception.class)
    public String handleException(){
        return "redirect:/405";
    }

    //----------------------------------------------------------------------------------------------------------------

    private User validateSession(HttpSession session){
        User user = (User) session.getAttribute("userlogin");
        if (user == null)
            throw new IllegalArgumentException("Permission Denied");
        else return user;
    }
}
