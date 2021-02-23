package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.DAO.EventResponseDao;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.EventSanitised;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
public class EventControllerAPI {

    private static final Logger LOGGER=LoggerFactory.getLogger(EventControllerAPI.class);

    @Autowired
    EventService eventService;

    @Autowired
    ValidationService userValidation;

    @Autowired
    UserService userService;

    @Autowired
    EventResponseService responseService;

    @Autowired
    EventResponseDao responseData;

    // Request Mappings
    //-----------------------------------------------------------------

    // Return all Events
    @GetMapping("api/Events")
    public Collection<Event> API_GetAllEvents(){
        return eventService.getEvents();
    }

    // Return users events
    @GetMapping("api/MyEvents")
    public Collection<EventSanitised> API_GetMyEvents(@RequestParam(value="Token") String token) {
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user UUID
            String UserUUID = userValidation.getUserUUIDfromToken(token);
            Collection<Event> events = eventService.getUserEvents(UserUUID);
            Collection<EventSanitised> sanitisedEvents = new ArrayList<>();

            for(Event e : events){
                sanitisedEvents.add(e.convertToSanitised());
            }
            return sanitisedEvents;
        }else{
            String errorText = (String) response[1];
            EventSanitised error = new EventSanitised(errorText, errorText, errorText);
            List<EventSanitised> ErrorReturn = new ArrayList<>();
            ErrorReturn.add(error);
            return ErrorReturn;
        }
    }

    // Get event by specific eventUUID
    @GetMapping("api/Event/{UUID}")
    public EventSanitised API_GetEvent(@PathVariable(value="UUID") String UUID){
        return eventService.getEvent(UUID).convertToSanitised();
    }

    @GetMapping("api/Event/{eventUUID}/times")
    public List<DateTimeRange> getTimes(@PathVariable(value = "eventUUID") String eventUUID){
        return eventService.getTimeRange(UUID.fromString(eventUUID));
    }

    // POST Mappings
    //-----------------------------------------------------------------

    // Create event
    @PostMapping("api/Event")
    public String API_AddEvent(@RequestParam(value="Name") String Name, @RequestParam(value="Desc") String desc,
                               @RequestParam(value="Location") String location,
                               @RequestParam(value="Token", defaultValue="") String token){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);
            if(user == null){
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }
            return eventService.createEvent(Name, desc, location, user.getStringUUID());
        }else{
            String errorText = (String) response[1];
            return errorText;
        }

    }

    // PUT Mappings
    //-----------------------------------------------------------------

    // set users permisions on an event
    @PutMapping("/api/Event/perms/{EventUUID}")
    public String setPermisionLevel(@RequestParam(value = "Token", defaultValue = "") String token,
                                    @PathVariable(value = "EventUUID") String eventUUID,
                                    @RequestParam(value = "Owner", defaultValue = "false") boolean owner){
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if (result){
            User user = userValidation.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            return (eventService.setPermissions(eventService.getEvent(eventUUID), user , owner)) ? "Permisions updated" : "Permisions not updated";         
        }
        else{
            // return the user validation error
            return (String) response[1];
        }
    }

    // set Event Times
    @PutMapping("/api/Event/{EventUUID}/times")
    public String setEventTimes(
                                @RequestParam(value = "Token", defaultValue = "") String token,
                                @PathVariable(value = "EventUUID") String eventUUID,
                                @RequestBody List<DateTimeRange> datetime){
         
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if (result){
            User user = userValidation.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }
            return (eventService.setTimeRange(UUID.fromString(eventUUID), datetime)) ? "Event times set" : "Event TImes not set";         
        }
        else{
            // return the user validation error
            return (String) response[1];
        }
    }


    // Join event
    @PutMapping("api/Event/{EventUUID}")
    public String API_AddUserToEvent(@RequestParam(value="Token", defaultValue ="") String token,
                                     @PathVariable(value="EventUUID") String EventUUID) {

        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Add user to event
            return eventService.joinEvent(EventUUID, user.getStringUUID());

        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }

    @PutMapping("api/Event/{EventUUID}/Leave")
    public String API_LeaveEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){
        // Validate token
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);

            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Leave event
            return eventService.leaveEvent(EventUUID, user.getStringUUID());

        }else{
            return "Token not valid.";
        }
    }

    // DELETE Mappings
    //-----------------------------------------------------------------

    // Delete event
    @DeleteMapping("api/Event/{EventUUID}")
    public String API_DeleteEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String EventUUID){

        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if(result){
            // Get user
            User user = userValidation.getUserFromToken(token);

            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            // Add user to event
            return eventService.deleteEvent(EventUUID, user);

        }else{
            String errorText = (String) response[1];
            return errorText;
        }
    }


    /* Event Responses */

    // General
    //-----------------------------------------------------------------

    // Get all responses to an event
    @GetMapping("api/Event/{EventUUID}/Response")
    public ResponseEntity<List<EventResponse>> getResponses(
        @PathVariable(value = "EventUUID") String eventUUID){
        
        try{
            List<EventResponse> eventResponse = responseData.get(UUID.fromString(eventUUID)).orElseThrow(IllegalArgumentException::new);
            return new ResponseEntity<List<EventResponse>>(eventResponse,HttpStatus.OK);
        }
        catch (IllegalArgumentException e){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        catch(Exception e){return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    // Get all users on an event
    @GetMapping("api/Event/{EventUUID}/Response/Users")
    public ResponseEntity<List<UserSanitised>> getResponsesUsers(
        @PathVariable(value = "EventUUID") String eventUUID){
        
        try{
            ArrayList <UserSanitised> eventUsers = new ArrayList<>();
            List<EventResponse> eventResponse = responseData.get(UUID.fromString(eventUUID)).orElseThrow(IllegalArgumentException::new);

            for (EventResponse r : eventResponse){
                User user = userService.getUserByUUID(r.getUser().toString());
                eventUsers.add(user.convertToUser());
            }
            return new ResponseEntity<List<UserSanitised>>(eventUsers,HttpStatus.OK);
        }
        catch (IllegalArgumentException e){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        catch(Exception e){
            LOGGER.error("Could not complete request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @PutMapping("api/Event/{EventUUID}/Response/User")
    public ResponseEntity<String> addUser(
        @PathVariable(value = "EventUUID") String eventUUID,
        @RequestParam(value = "UserUUID") String userUUID,
        @RequestParam(value = "required") Boolean userRequired){
        
        try{

            responseService.createResponse(userService.getUserByUUID(userUUID), eventService.getEvent(eventUUID));

            //if (userRequired)
                //userService.setRequired()
            
            return new ResponseEntity<>(HttpStatus.OK);
            
        }
        catch (Exception e){
            LOGGER.error("Could not complete request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Times
    //-----------------------------------------------------------------

    // Get times
    @GetMapping("api/Event/{EventUUID}/Response/time")
    public ResponseEntity<List<DateTimeRange>> responseTimeGet(
        @RequestParam(value = "Token") String token,
        @PathVariable(value = "EventUUID") String eventUUID){

        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if (! (boolean) response[0]) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            User user = userValidation.getUserFromToken(token);
            
            List<DateTimeRange> times = responseService.getTimes(responseData.get(UUID.fromString(eventUUID), user.getUUID()).get()).get();
            return new ResponseEntity<List<DateTimeRange>>(times, HttpStatus.OK);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception e){
            LOGGER.error("Failed to process request: {}", e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Set times
    @PutMapping("api/Event/{EventUUID}/Response/time")
    public ResponseEntity<String> responseTimeSet(
        @RequestParam(value = "Token") String token,
        @PathVariable(value = "EventUUID") String eventUUID,
        @RequestBody List<DateTimeRange> times){

        Object[] response = userValidation.verifyAPItoken(token);

        if (! (boolean) response[0]) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            User user = userValidation.getUserFromToken(token);
            if (responseService.setTimes(responseData.get(UUID.fromString(eventUUID), user.getUUID()).get(), times)){
                return new ResponseEntity<String>("Times set",HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            LOGGER.error("Failed to process request: {}", e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Clear times
    @DeleteMapping("api/Event/{EventUUID}/Response/time")
    public ResponseEntity<String> responseTimeDelete(
        @RequestParam(value = "Token") String token,
        @PathVariable(value = "EventUUID") String eventUUID){

        Object[] response = userValidation.verifyAPItoken(token);

        if (! (boolean) response[0]) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            User user = userValidation.getUserFromToken(token);
            Event event = eventService.getEvent(eventUUID);

            return responseService.clearTimes(responseData.get(UUID.fromString(eventUUID), user.getUUID()).get()) ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e){
            LOGGER.error("Failed to process request: {}", e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }   
    }
}
