package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.Model.DTO.DTO;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.ResultProperty;
import com.LetsMeet.LetsMeet.Event.Model.Properties.GradedProperty;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.Event.Service.EventResultService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.WeatherService;
import com.LetsMeet.LetsMeet.Venue.Service.VenueService;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.*;

@Controller
@RequestMapping("/event/{eventUUID}/results")
@SessionAttributes("userlogin")
public class ResultControllerWeb {

    private static final Logger LOGGER= LoggerFactory.getLogger(ResultControllerWeb.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private EventResultService resultsService;

    @Autowired
    private PollService pollService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private WeatherService weatherService;


    @GetMapping("/time")
    public String httpTimeGet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventUUID,
        @RequestParam(value = "duration", defaultValue = "30") int duration,
        @RequestParam( value = "attendance", defaultValue = "90") int attendance,
        @RequestParam( value = "requiredUsers", defaultValue = "false") boolean requiredUsers) {

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventUUID)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            model.addAttribute("user", user);
            model.addAttribute("event", event);

            model.addAttribute("results",resultsService.calculateTimes(event, duration,requiredUsers));

            return "event/results/time";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/time")
    public String resultsTimeSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
                                    @PathVariable("eventUUID") String eventuuid,
                                    @RequestParam(value = "timeIndex") int timeIndex){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            resultsService.selectTimes(event, timeIndex);
            redirectAttributes.addFlashAttribute("success", "Date and time confirmed!");

            if (resultsService.getResult(event).orElseThrow().getLocations().getSelected().isEmpty()){
                redirectAttributes.addFlashAttribute("info", "Location has not been confirmed. Select your location from below");
                return "redirect:/event/{eventUUID}/results/location?attendance=30&requiredUsers=false";
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

    @GetMapping("/location")
    public String eventResultsLocation(Model model, RedirectAttributes redirectAttributes, HttpSession session,
       @PathVariable("eventUUID") String eventuuid,
       @RequestParam(value = "duration", defaultValue = "30") int duration,
       @RequestParam( value = "attendance", defaultValue = "90") int attendance,
       @RequestParam( value = "requiredUsers", defaultValue = "false") boolean requiredUsers) {

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            model.addAttribute("user", user);
            model.addAttribute("event", eventService.get(UUID.fromString(eventuuid)).orElseThrow());
            var results = resultsService.calculateLocation(event, attendance,requiredUsers);
            model.addAttribute("results", results);
            return "event/results/location";
        }
        catch(Exception e){
            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());
            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/location")
    public String resultsLocationSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "locationIndex") int locationIndex,
        @RequestParam(value = "skipVenue", defaultValue="true") boolean skipVenue){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
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

    private static String LOCATION_NOT_FOUND = "Location not found";
    private static String DATETIME_NOT_FOUND = "Date/Time not found";

    @GetMapping("/venue")
    public String eventResultsLocation(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "facilities", defaultValue = "") String facilities,
        @RequestParam(value = "isOpen", defaultValue = "false") boolean isOpen){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            var results = resultsService.getResult(event).orElseThrow();
            var location = results.getLocations().getSelected().orElseThrow( () -> new IllegalArgumentException(LOCATION_NOT_FOUND)).getProperty();
            var date = results.getDates().getSelected().orElseThrow(()-> new IllegalArgumentException(DATETIME_NOT_FOUND)).getProperty().getStart();

            // Build and execute search
            List<Venue> venues = venueService.search(
                    "",
                    venueService.formatFacilitiesForSearch(facilities),
                    "",
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getRadius()),
                    "",
                    (isOpen) ? String.valueOf(date.getHour()) : "",
                    (isOpen) ? String.valueOf(date.getMinute()) : "",
                    (isOpen) ? date.getDayOfWeek().toString(): "");

            System.out.println(date);
            // Calculate days in future

            LocalDate eventDate = date.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(currentDate, eventDate);
            System.out.println ("Days: " + daysBetween);
            if(daysBetween <= 7) {
                for (Venue v : venues) {
                    JsonObject forecast = weatherService.getWeatherForecast(v.getLatitude(), v.getLongitude(), 0);
                    String temp = null;
                    // Get event hour
                    int hour = date.getHour();
                    if(hour < 11){
                        temp = forecast.get("morn").toString();
                    }else if(hour >= 11 && hour < 16){
                        temp = forecast.get("day").toString();
                    }else if(hour >= 16 && hour < 20){
                        temp = forecast.get("eve").toString();
                    }else if (hour >= 20){
                        // Night
                        temp = forecast.get("night").toString();
                    }

                    v.setCurrentTemperature(temp);
                }
            }


            model.addAttribute("user", user);
            model.addAttribute("event", eventService.get(UUID.fromString(eventuuid)).orElseThrow());
            model.addAttribute("venues", venues);
            model.addAttribute("options", event.getEventProperties().getFacilities());
            return "event/results/venue";
        }
        catch(Exception e){
            LOGGER.error("Could not set times User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            e.printStackTrace();

            if (e.getMessage().contains(LOCATION_NOT_FOUND)) redirectAttributes.addFlashAttribute("warning", LOCATION_NOT_FOUND);
            if (e.getMessage().contains(DATETIME_NOT_FOUND)) redirectAttributes.addFlashAttribute("warning",DATETIME_NOT_FOUND);
            redirectAttributes.addFlashAttribute("danger", "An error occurred");

            return "redirect:/event/{eventUUID}";
        }
    }

    @PostMapping("/venue")
    public String resultsVenueSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session,
        @PathVariable("eventUUID") String eventuuid,
        @RequestParam(value = "venueUUID") String venueUUID){

        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
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

    @PostMapping("/confirm")
    public String httpResultsConfirm(Model model, RedirectAttributes redirectAttributes, HttpSession session, @PathVariable("eventUUID") String eventuuid, @RequestParam(name = "message") String message ){
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        resultsService.sendConfirmation(event, user, message);
        return "redirect:/event/{eventUUID}/results";

    }

    @GetMapping()
    public String resultsVenueSelect(Model model, RedirectAttributes redirectAttributes, HttpSession session, @PathVariable("eventUUID") String eventuuid){
        User user = (User) session.getAttribute("userlogin");
        Event event = eventService.get(UUID.fromString(eventuuid)).orElseThrow();
        if (user == null || event == null){
            redirectAttributes.addFlashAttribute("danger", "An error occurred.");
            return "redirect:/event/{eventUUID}";
        }

        try{
            var result = resultsService.getResult(event).orElseGet(() -> resultsService.newEventResult(event));

            if (result.getDates().getSelected().isEmpty()){
                redirectAttributes.addFlashAttribute("info", "Date and times have not been confirmed. Select your preference from the suggestions below");
                return "redirect:/event/{eventUUID}/results/time?duration=10&attendance=10";
            }
            else if (result.getLocations().getSelected().isEmpty()){
                redirectAttributes.addFlashAttribute("info", "Location has not been confirmed. Select your preference from the suggestions below");
                return "redirect:/event/{eventUUID}/results/location";
            }


            model.addAttribute("result", result);
            model.addAttribute("venue", venueService.getVenue(result.getVenueUUID().toString()));
            model.addAttribute("event", event);

            // Add polls to model
            List<DTO.PollData> polls = new ArrayList<>();
            for (var poll : eventService.getPolls(event)){
                polls.add(new DTO.PollData(poll));
            }
            model.addAttribute("polls", polls);

            return "event/results/overview";
        }
        catch(Exception e){

            //if (e.getMessage().contains("load event result"))
            //    return "redirect:/event/{eventUUID}/results/time?duration=10&attendance=10";

            LOGGER.error("Could not view results User<{}> Event<{}>: {}", user.getUUID(),event.getUUID(),e.getMessage());
            redirectAttributes.addFlashAttribute("danger", "An error occurred: " + e.getMessage());

            return "redirect:/event/{eventUUID}";

        }
    }

}
