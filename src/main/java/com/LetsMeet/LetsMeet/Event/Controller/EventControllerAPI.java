package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.DAO.EventResponseDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

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
    public ResponseEntity<Collection<Event>> getActiveUserEvents(@RequestParam(value="Token") String token) {
        try{
            User user = userValidation.getAuthenticatedUser(token);
            return new ResponseEntity<>(eventService.getUserEvents(user),HttpStatus.OK);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        catch(Exception e){
            LOGGER.error("Could not fetch all events: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get event by specific eventUUID
    @GetMapping("api/Event/{UUID}")
    public ResponseEntity<Event> API_GetEvent(@PathVariable(value="UUID") String eventUUID){
        try{
            return new ResponseEntity<>(eventService.getEvent(eventUUID), HttpStatus.OK);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("api/Event/{eventUUID}/time")
    public ResponseEntity<List<DateTimeRange>> getTimes(@PathVariable(value = "eventUUID") String eventUUID){
        try{
            return new ResponseEntity<>(eventService.getTimeRange(UUID.fromString(eventUUID)),HttpStatus.OK);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("api/Event/{eventUUID}/results")
    public ResponseEntity<String> getResults(
        @PathVariable(value = "eventUUID") String eventUUID, 
        @RequestParam(value = "Token", defaultValue = "") String token){

        try{
            userValidation.getAuthenticatedUser(token);
            Event event = eventService.getEvent(eventUUID);

            
            return new ResponseEntity<>(eventService.getProperty(event, "results.time"),HttpStatus.OK);
        }
        catch (Exception e){
            LOGGER.error("Unable to calculate results for Event <{}> : {}", eventUUID,e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

            try {
                //eventService.createEvent(Name, desc, location, user.getStringUUID());
                eventService.save(new Event(Name), user);
                return "Event successfully created.";
            }catch (Exception e){
                return "Error creating event";
            }
        }else{
            String errorText = (String) response[1];
            return errorText;
        }

    }

    // PUT Mappings
    //-----------------------------------------------------------------

    // set users permissions on an event
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
    @PutMapping("/api/Event/{EventUUID}/time")
    public String setEventTimes(
            @RequestParam(value = "Token", defaultValue = "") String token,
            @PathVariable(value = "EventUUID") String eventUUID,
            @RequestBody List<HashMap<String,String>> times){
         
        Object[] response = userValidation.verifyAPItoken(token);
        boolean result = (boolean) response[0];

        if (result){
            User user = userValidation.getUserFromToken(token);
            if (user == null) {
                return "Error finding user. Is the token still valid? Is the user account still active?";
            }

            ArrayList<DateTimeRange> timeRanges = new ArrayList<>();
            for (HashMap<String, String> o : times){
                timeRanges.add(new DateTimeRange(ZonedDateTime.parse(o.get("start")), ZonedDateTime.parse(o.get("end"))));
            }

            return (eventService.setTimeRange(eventService.getEvent(eventUUID), timeRanges)) ? "Event times set" : "Event TImes not set";         
        }
        else{
            // return the user validation error
            return (String) response[1];
        }
    }



    // PATCH Mappings
    //-----------------------------------------------------------------
    @PatchMapping("api/Event/{EventUUID}")
    public ResponseEntity<String> API_UpdateEvent(@RequestParam(value="Token", defaultValue="") String token,
                                  @PathVariable(value="EventUUID") String EventUUID,
                                  @RequestParam(value="Name", defaultValue="") String name,
                                  @RequestParam(value="Description", defaultValue="") String desc,
                                  @RequestParam(value="Location", defaultValue="") String location){
        try{
            // Get user
            User user = userValidation.getAuthenticatedUser(token);

            // Get event
            Event event = eventService.getEvent(EventUUID);
            event.setName(name);
            event.setDescription(desc);
            event.setLocation(location);

            eventService.update(user, event);
            return new ResponseEntity<>(HttpStatus.OK);

        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE Mappings
    //-----------------------------------------------------------------

    // Delete event
    @DeleteMapping("api/Event/{EventUUID}")
    public ResponseEntity<Boolean> API_DeleteEvent(@RequestParam(value="Token") String token, @PathVariable(value="EventUUID") String eventUUID){
        try{
            // Get user
            User user = userValidation.getAuthenticatedUser(token);
            Event event = eventService.getEvent(eventUUID);

            // Add user to event
            return new ResponseEntity<>(eventService.deleteEvent(event, user),HttpStatus.OK);

        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Properties
    //-----------------------------------------------------------------
    @GetMapping("api/Event/{EventUUID}/Properties")
    public ResponseEntity<String> eventGetProperty(
        @PathVariable(value = "EventUUID") String eventUUID,
        @RequestParam(value = "key") String propertyKey){
        
        return new ResponseEntity<>(eventService.getProperty(eventService.getEvent(eventUUID), propertyKey), HttpStatus.OK);      
    }

    @PutMapping("api/Event/{EventUUID}/Properties")
    public ResponseEntity<String> eventGetProperty(
        @PathVariable(value = "EventUUID") String eventUUID,
        @RequestParam(value = "key") String propertyKey,
        @RequestParam(value = "value") String value){
        
        try{
            eventService.setProperty(eventService.getEvent(eventUUID), propertyKey, value);
            return new ResponseEntity<>(HttpStatus.OK); 
            
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    // Location
    //-----------------------------------------------------------------
    @GetMapping("api/Event/{EventUUID}/location")
    public ResponseEntity<Object> getLocation(@PathVariable(value = "EventUUID") String eventUUID){
        try{
        Event event =  eventService.getEvent(eventUUID);
        return new ResponseEntity<>(event.getEventProperties().getLocation(),HttpStatus.OK);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/Event/{EventUUID}/location")
    public ResponseEntity<Object> setLocation(
                                @RequestParam(value = "Token", defaultValue = "") String token,
                                @PathVariable(value = "EventUUID") String eventUUID,
                                @RequestBody List<Location> locations){
         
        try{
            Event event = eventService.getEvent(eventUUID);
            event.getEventProperties().setLocation(locations.get(0));
            eventService.update(userValidation.getAuthenticatedUser(token), event);
            return new ResponseEntity<>(event.getEventProperties(),HttpStatus.OK);
            
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
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

    // Create a response
    @PutMapping("api/Event/{EventUUID}/Response")
    public ResponseEntity<String> makeResponse(
        @PathVariable(value = "EventUUID") String eventUUID, 
        @RequestParam(value = "user", defaultValue = "default") String userUUID, 
        @RequestParam(value = "Token") String token){
        
        Object[] response = userValidation.verifyAPItoken(token);

        if (! (boolean) response[0]) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{

            if (userUUID.equals("default")){
                userUUID = userValidation.getUserUUIDfromToken(token);
            }

            responseService.createResponse(userService.getUserByUUID(userUUID), eventService.getEvent(eventUUID),false);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            LOGGER.error("Could not complete request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("api/Event/{EventUUID}/Response")
    public ResponseEntity<String> deleteResponse(@PathVariable(value = "EventUUID") String eventUUID, HttpSession session){

        try{    
            User user = (User) session.getAttribute("userlogin");
            Event event = eventService.getEvent(eventUUID);

            responseService.deleteResponse(user, event);

            return new ResponseEntity<>(HttpStatus.OK);
        }        
        catch (Exception e){
            LOGGER.error("Could not complete request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @PutMapping("api/Event/{EventUUID}/Response/{UserUUID}")
    public ResponseEntity<String> addUser(
        @PathVariable(value = "EventUUID") String eventUUID,
        @PathVariable(value = "UserUUID") String userUUID,
        @RequestParam(value = "required", defaultValue = "false") Boolean userRequired){
        
        try{
            User user = userService.getUserByUUID(userUUID);
            Event event = eventService.getEvent(eventUUID);
            EventResponse response = responseService.getResponse(user, event).orElseGet(
                () -> responseService.createResponse(user, event, userRequired));
            response.setRequired(userRequired);
            responseService.saveResponse(response);
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
        @RequestBody List<HashMap<String,String>> times){

        Object[] response = userValidation.verifyAPItoken(token);

        if (! (boolean) response[0]) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            User user = userValidation.getUserFromToken(token);
            Event event = eventService.getEvent(eventUUID);

            EventResponse eventResponse = responseService.getResponse(user, event).orElseGet(() -> responseService.createResponse(user, event, false));

            // Create list of TimeRange
            ArrayList<DateTimeRange> timeRanges = new ArrayList<>();
            for (HashMap<String, String> o : times){
                timeRanges.add(new DateTimeRange(ZonedDateTime.parse(o.get("start")), ZonedDateTime.parse(o.get("end"))));
            }

            // Update response and return
            return responseService.setTimes(eventResponse, timeRanges) ? new ResponseEntity<>("Times set",HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

            return responseService.clearTimes(responseData.get(UUID.fromString(eventUUID), user.getUUID()).get()) ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e){
            LOGGER.error("Failed to process request: {}", e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }   
    }
}
