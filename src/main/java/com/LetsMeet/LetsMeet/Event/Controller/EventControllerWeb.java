package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventResultService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import static com.LetsMeet.LetsMeet.Utilities.MethodService.deepCopyStringList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.*;
import java.util.*;

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

    @Autowired
    EventResultService resultsService;

    @Autowired
    VenueService venueService;

    @Autowired
    ValidationService validationService;

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
                               @RequestParam(name = "eventlocation") String eventlocation,
                               @RequestParam(name="jsonTimes") String tRanges){

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
                var path= mediaService.newMedia(file, "event", "banner").orElseThrow();
                eventService.setProperty(event, "header_image", mediaService.generateURL(path));
            }

            // Update time Ranges
            // Format input data to DateTimeRange objects
            List<DateTimeRange> ranges = EventService.processJsonRanges(tRanges);

            // Add time ranges to Event
            eventService.setTimeRange(event, ranges);

            eventDao.update(event);
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Could not update event. Please try again later.");
            e.printStackTrace();
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
        @RequestParam(name = "eventlocation") String eventlocation, @RequestParam(name = "thelat") double eventLatitude,
                            @RequestParam(name = "thelong") double eventLongitude, @RequestParam(name = "radius") String eventRadius,
                            @RequestParam(name = "startDays") List<String> startDay, @RequestParam(name="startTimes") List<String> startTime,
                            @RequestParam(name="jsonTimes") String tRanges) {

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

            // Create new Event object and persist it
            //Event event = eventService.createEvent(eventname, eventdesc, eventlocation, user.getUUID().toString());
            Event event = new Event(eventname);
            eventService.save(event, user);

            /* Setup and add Event Times */

            // Check input data
            if (startDay.isEmpty() || startTime.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning","Specify one or more event times.");
                return  "redirect:/event/new";
            }

            // Format input data to DateTimeRange objects
            List<DateTimeRange> ranges = EventService.processJsonRanges(tRanges);

            // Add time ranges to Event
            eventService.setTimeRange(event, ranges);

            /* Setup and add Image */

            // Store and set header image file if present
            if (file.getSize()>0){
                var path= mediaService.newMedia(file, "event", "banner").orElseThrow();
                eventService.setProperty(event, "header_image", "media/" + mediaService.generateURL(path));    
            }

            /* Setup and add Location */
            eventService.setLocation(event, new Location(eventlocation, eventLatitude, eventLongitude, 50000.0));

            /* Update event to persist changes */
            eventDao.update(event);
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

            // Get event times
            List<List<String>> times = new ArrayList<>();
            List<String> arr = new ArrayList<>();
            int rows = -1;
            for(DateTimeRange t : event.getEventProperties().getTimes()){
                rows += 1;
                arr.clear();
                // Start date
                ZonedDateTime s = t.getStart();
                arr.add(String.format("%s-%s-%s",s.getYear(), s.getMonthValue(), s.getDayOfMonth()));

                // Start time
                int hour = s.getHour();
                String h;
                if(hour < 10){
                    h = String.format("0%s", hour);
                }else{
                    h = Integer.toString(hour);
                }

                int minute = s.getMinute();
                String m;
                if(minute < 10){
                    m = String.format("0%s", minute);
                }else{
                    m = Integer.toString(minute);
                }

                int second = s.getSecond();
                String sec;
                if(second < 10){
                    sec = String.format("0%s", second);
                }else{
                    sec = Integer.toString(second);
                }

                arr.add(String.format("%s:%s:%s", h, m, sec));

                // End date
                ZonedDateTime e = t.getEnd();
                arr.add(String.format("%s-%s-%s",e.getYear(), e.getMonthValue(), e.getDayOfMonth()));

                // End time
                hour = e.getHour();
                if(hour < 10){
                    h = String.format("0%s", hour);
                }else{
                    h = Integer.toString(hour);
                }

                minute = e.getMinute();
                if(minute < 10){
                    m = String.format("0%s", minute);
                }else{
                    m = Integer.toString(minute);
                }

                second = e.getSecond();
                if(second < 10){
                    sec = String.format("0%s", second);
                }else{
                    sec = Integer.toString(second);
                }

                arr.add(String.format("%s:%s:%s", h, m, sec));

                // Add input ID's
                arr.add(String.format("startDay%d", rows));
                arr.add(String.format("startTime%d", rows));
                arr.add(String.format("endDay%d", rows));
                arr.add(String.format("endTime%d", rows));

                times.add(deepCopyStringList(arr));
            }
            model.addAttribute("times", times);
            model.addAttribute("rows", rows);

            return "viewevent";
        }else {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        }

    }

    @PostMapping("/event/{eventUUID}/users")
    public String eventUsers(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(value="usersRequired") List<String> userInputs){
        try{
            Event event = eventService.getEvent(eventUUID);
            List<User> users = new ArrayList<>();
            User invitedUser = null;

            for ( var v : userInputs){
                // Attempts to get by uuid
                try {
                    invitedUser = userService.getUserByUUID(v);
                }catch(Exception e){
                    // We dont care about this
                }

                if(invitedUser == null){
                    // Attempt to get by email
                    invitedUser = userService.getUserByEmail(v);
                }

                if(invitedUser == null){
                    // User is not in DB
                    // Check if email has been input
                    boolean emailFormat = validationService.checkEmailMakeUp(v);
                    if(emailFormat){
                        // Send invite to this email - for guest
                        invitedUser = userService.createGuest(v, event);
                    }
                }

                if(!(invitedUser == null)){
                    // Add user to list
                    users.add(invitedUser);
                }
            }

            for (User user : users){
                responseService.createResponse(user, event, false);
                if(user.getIsGuest()){
                    // Email guest
                    String guestLink = String.format("localhost:8080/event/%s/respond/%s", event.getUUID().toString(), user.getUUID().toString());
                    LOGGER.info(guestLink);
                }else{
                    // Email regular user

                }
            }
            redirectAttributes.addFlashAttribute("success","Invitation sent!");
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("danger","Failed to invite users: " + e.getMessage());
        }
        return "redirect:/event/{eventUUID}";
    }

    @GetMapping("/event/{eventUUID}/results/time")
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

            
            model.addAttribute("results",resultsService.calculateTimes(event, duration,requiredUsers));
            
            return "event/results";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/event/{eventUUID}/results/time")
    public String resultsTimeSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "timeIndex") int timeIndex){
        
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            resultsService.selectTimes(event, timeIndex);
            redirectAttributes.addFlashAttribute("success", "Date and time confirmed!");

            if (!resultsService.getResult(event).getLocations().getSelected().isPresent()){
                redirectAttributes.addFlashAttribute("info", "Location has not been confirmed. Select your location from below");
                return "redirect:/event/{eventUUID}/results/location";
            }

            else
                return "redirect:/event/{eventUUID}/results";
        }
        catch(Exception e){
            LOGGER.error("Could not set times User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
  
    }

    @GetMapping("/event/{eventUUID}/results/location")
    public String eventResultsLocation(Model model, RedirectAttributes redirectAttributes, HttpSession session,
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
            model.addAttribute("results",resultsService.calculateLocation(event, duration,requiredUsers));
            return "event/results/location";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/event/{eventUUID}/results/location")
    public String resultsLocationSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "locationIndex") int locationIndex,
        @RequestParam(value = "skipVenue", defaultValue="true") boolean skipVenue){
        
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            resultsService.selectLocation(event, locationIndex);
            redirectAttributes.addFlashAttribute("success", "Location confirmed!");

            if (skipVenue) {return "redirect:/event/{eventUUID}/results";}

            redirectAttributes.addFlashAttribute("info", "Venue has not yet been confirmed. Select your preferred venue from below");
            return "redirect:/event/{eventUUID}/results/venue";
            
        }
        catch(Exception e){
            LOGGER.error("Could not set times User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
  
    }

    @GetMapping("/event/{eventUUID}/results/venue")
    public String eventResultsLocation(Model model, RedirectAttributes redirectAttributes, HttpSession session, @PathVariable("eventUUID") String eventuuid){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }
        
        try{
            var results = resultsService.getResult(event);
            var venues = venueService.searchByRadius(results.getLocations().getSelected().get().getProperty().getLongitude(), results.getLocations().getSelected().get().getProperty().getLatitude(), results.getLocations().getSelected().get().getProperty().getRadius());

            model.addAttribute("user", user);
            model.addAttribute("event", eventService.getEvent(eventuuid));
            model.addAttribute("venues", venues);
            return "event/results/venue";
        }
        catch(Exception e){
            LOGGER.error("Could not set times User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/event/{eventUUID}/results/venue")
    public String resultsVenueSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "venueUUID") String venueUUID){
        
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            resultsService.setVenue(event, UUID.fromString(venueUUID));
            redirectAttributes.addFlashAttribute("success", "Venue confirmed!");

             return "redirect:/event/{eventUUID}/results";
            
        }
        catch(Exception e){
            LOGGER.error("Could not set venue User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/event/{eventUUID}/results/confirm")
    public String httpResultsConfirm(Model model, RedirectAttributes redirectAttributes, HttpSession session, @PathVariable("eventUUID") String eventuuid, @RequestParam(name = "message") String message ){
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        resultsService.sendConfirmation(event, user, message);
        return "redirect:/event/{eventUUID}/results";
            
    }

    @GetMapping("/event/{eventUUID}/results")
    public String resultsVenueSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session, @PathVariable("eventUUID") String eventuuid){
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            var result = resultsService.getResult(event);
            if (result.getDates().getSelected().isEmpty()){
                redirectAttributes.addFlashAttribute("info", "Date and times have not been confirmed. Select your preference from the suggestions below");
                return "redirect:/event/{eventUUID}/results/time?duration=10&attendance=10";
            }

            model.addAttribute("result", result);
            model.addAttribute("venue", venueService.getVenue(result.getVenueUUID().toString()));
            model.addAttribute("event", event);
            return "event/results/overview";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}/results/time?duration=10&attendance=10";
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

    @PostMapping("/event/{eventUUID}/response")
    public String saveResponse(@PathVariable("eventUUID") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session,
                               @RequestParam(value="jsonTimes") String jsonTimeRanges, @RequestParam(value="responselocation", defaultValue="") String address,
                               @RequestParam(value="thelat", defaultValue="") String lat,
                               @RequestParam(value="thelong", defaultValue="") String longitude,
                               @RequestParam(value="responsefacilities", defaultValue = "") String facilities,
                               @RequestParam(value="radius", defaultValue="") String radius,
                               @RequestParam(value="userUUID", defaultValue="") String useruuid){
        // Get user
        User user = (User) session.getAttribute("userlogin");
        if(user == null && !(useruuid.equals(""))){
            user = userService.getUserByUUID(useruuid);
        }

        Event event = eventService.getEvent(eventuuid);

        String destination;
        if(user.getIsGuest()){
            destination = String.format("redirect:/Home");
        }else {
            destination = String.format("redirect:/event/%s", eventuuid);
        }

        // Check user is invited to event
        Optional<EventResponse> record = responseService.getResponse(user, event);
        if(record.isPresent()) {
            EventResponse response = record.get();
            EventProperties properties = response.getEventProperties();

            // Process time ranges
            System.out.println("jsonTimeRanges:");
            System.out.println(jsonTimeRanges);
            List<DateTimeRange> ranges = EventService.processJsonRanges(jsonTimeRanges);

            properties.setTimes(ranges);

            // Process location
            // Check all values are in order
            try {
                Location location = properties.getLocation();
                Double dlat = Double.parseDouble(lat);
                Double dlong = Double.parseDouble(longitude);
                Double dRadius = Double.parseDouble(radius);

                if(!address.equals("")) {
                    location.setName(address);
                    location.setLatitude(dlat);
                    location.setLongitude(dlong);
                    location.setRadius(dRadius);
                    properties.setLocation(location);
                }
            }catch(Exception e){
                // Ignore
            }

            // Process facilities
            if(!facilities.equals("")) {
                try {
                    List<String> facilityList = Arrays.asList(facilities.split(","));
                    properties.setFacilities(facilityList);
                } catch (Exception e) {
                    System.out.println("VenueService.Search");
                    System.out.println(e);
                }
            }

            // Save response
            response.setEventProperties(properties);
            responseService.saveResponse(response);

            System.out.println(response.getEventProperties().getLocation().getRadius());
            // Redirect to event page
            redirectAttributes.addFlashAttribute("alert alert-success", "Response given.");

        }
        return destination;
    }

    @GetMapping("/event/{eventuuid}/respond2")
    public String respondEvent2(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.getEvent(eventuuid);

        if (user != null){
            model.addAttribute("user", user);
            model.addAttribute("event", event);
        }

        else { 
            LOGGER.error("Response error");
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");return "redirect:/Home";
        }

        // Check if user has already responded (they can edit previous responses)
        Optional<EventResponse> response = responseService.getResponse(user, event);
        if(response.isPresent()){
            if(response.get().hasResponded()){
                model.addAttribute("response", response.get());
                EventProperties eventProperties = response.get().getEventProperties();
                List<DateTimeRange> times = eventProperties.getTimes();

                List<List<String>> strtimes = eventService.processTimeRanges(event);

                model.addAttribute("times", strtimes);
                model.addAttribute("numtimes", strtimes.size()-1);

                // facilities
                List<String> facilities = eventProperties.getFacilities();
                if(facilities.size() > 0){
                    model.addAttribute("facilities", facilities);
                }else{
                    model.addAttribute("facilities", null);
                }
            }else{
                model.addAttribute("response", null);
            }
        }else{
            model.addAttribute("response", null);
        }

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

    @GetMapping("/event/{eventuuid}/respond/{userUUID}")
    public String guestRespondEventPreface(@PathVariable("eventuuid") String eventuuid, @PathVariable("userUUID") String userUUID,
                                    Model model, RedirectAttributes redirectAttributes, HttpSession session){
        // Get guest user
        User user = userService.getUserByUUID(userUUID);
        Event event = eventService.getEvent(eventuuid);

        if(user == null || event == null || !user.getIsGuest()){
            return "redirect:/404";
        }

        model.addAttribute("guest", user);
        model.addAttribute("event", event);
        String destination = String.format("/event/%s/responding/%s", eventuuid, userUUID);
        model.addAttribute("ContinueAsGuest", destination);

        return "event/guestResponsePreface";
    }

    @GetMapping("/event/{eventuuid}/responding/{userUUID}")
    public String guestRespondEvent(@PathVariable("eventuuid") String eventUUID, @PathVariable("userUUID") String userUUID,
                                    Model model, RedirectAttributes redirectAttributes, HttpSession session,
                                    @RequestParam(value="FirstName", defaultValue = "") String fname,
                                    @RequestParam(value="LastName", defaultValue = "") String lname){
        // Get guest user
        User guest = userService.getUserByUUID(userUUID);
        Event event = eventService.getEvent(eventUUID);
        if(guest == null || event == null){
            return "redirect:/Home";
        }

        // Check user has been invited to event
        Optional<EventResponse> response = responseService.getResponse(guest, event);

        if(response.isPresent()) {
            model.addAttribute("event", event);
            model.addAttribute("user", guest);

            // If first and or last name is given - add to user record
            userService.updateUser(guest, fname, lname, "");

            if(response.get().hasResponded()){
                model.addAttribute("response", response.get());
                EventProperties eventProperties = response.get().getEventProperties();
                List<DateTimeRange> times = eventProperties.getTimes();

                List<List<String>> strtimes = eventService.processTimeRanges(event);

                model.addAttribute("times", strtimes);
                model.addAttribute("numtimes", strtimes.size()-1);

                // facilities
                List<String> facilities = eventProperties.getFacilities();
                if(facilities.size() > 0){
                    model.addAttribute("facilities", facilities);
                }else{
                    model.addAttribute("facilities", null);
                }
            }else{
                model.addAttribute("response", null);
            }

            return "event/response";

        }else{
            return "redirect:/404";
        }
    }

    // Error catching
    @ExceptionHandler(Exception.class)
    public String handleException(){
        return "redirect:/405";
    }
}
