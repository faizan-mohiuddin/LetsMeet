package com.LetsMeet.LetsMeet.Event.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.DAO.*;
import com.LetsMeet.LetsMeet.Event.Model.*;
import com.LetsMeet.LetsMeet.Root.Notification.NotificationService;
import com.LetsMeet.LetsMeet.Root.Notification.Notifications;
import com.LetsMeet.LetsMeet.Root.Notification.Model.Notification;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventResultService {

    
    @Autowired
    EventResultDao resultDao;

    @Autowired
    EventService eventService;

    @Autowired
    EventResponseService responseService;

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;


    public EventResult newEventResult(Event event) throws IllegalArgumentException{
        try{
        EventResult result = new EventResult(event.getUUID());
        resultDao.save(result);
        return result;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Event Result already exists");
        }
    }

    public EventResult getResult(Event event) throws IllegalArgumentException{
        try{
            return resultDao.get(event.getUUID()).orElseThrow();
        }
        catch(Exception e){
            throw new IllegalArgumentException("Unable to load event result");
        }
    }

    public EventResult calculateResults(Event event, int duration, boolean requiredUsers) throws IllegalArgumentException{
        try{
            calculateTimes(event, duration, requiredUsers);
            calculateLocation(event,1,true);
            
            return resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate results: " + e.getMessage());
        }
    }

    public EventResult calculateTimes(Event event, int duration, boolean requiredUsers) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
            List<EventResponse> responses = responseService.getResponses(event);
            EventTimeSolver timeSolver = new EventTimeSolver(eventService.getEvent(event.getUUID().toString()), responses);


            List<EventResponse> requiredResponses = new ArrayList<>();
            for (EventResponse response : responses){
                if (response.getRequired().booleanValue()) requiredResponses.add(response);
            }

            timeSolver.solve(1);

            if(duration > 4) timeSolver.withDuration(Duration.ofMinutes(duration));
            if(requiredUsers) timeSolver.withResponses(requiredResponses);

            result.setUniqueResponses(responses.size());
            result.getDates().setGradedProperties(timeSolver.getSolution());
            resultDao.update(result);
            return result;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate times: " + e.getMessage() + " Note: Possible deserialisation failure");
        }
        
    }

    public void selectTimes(Event event, int timeIndex) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseThrow();
            var selected = result.getDates().getGradedProperties().get(timeIndex);
            result.getDates().setSelected(selected);
            resultDao.update(result);
        }
        catch(IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Could not select times: Selected time is out of range");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not select times: " + e.getMessage());
        }
    }

    public EventResult calculateLocation(Event event, int minOpt, boolean requiredUsers) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseGet(() -> newEventResult(event));
            List<EventResponse> responses = responseService.getResponses(event);
            EventLocationSolver locationSolver = new EventLocationSolver(eventService.getEvent(event.getUUID().toString()).getEventProperties(), responses);
            
            result.getLocations().setGradedProperties(locationSolver.solve());
            resultDao.update(result);
            return result;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not calculate location: " + e.getMessage());
        }
    }

    public void selectLocation(Event event, int locationIndex) throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseThrow();
            var selected = result.getLocations().getGradedProperties().get(locationIndex);
            result.getLocations().setSelected(selected);
            resultDao.update(result);
        }
        catch(IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Could not select location: Selected time is out of range");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not select location: " + e.getMessage());
        }
    }

    public void setVenue(Event event, UUID venueUUID)throws IllegalArgumentException{
        try{
            EventResult result = resultDao.get(event.getUUID()).orElseThrow();
            result.setVenueUUID(venueUUID);
            resultDao.update(result);
        }
        catch(IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Could not select location: Selected time is out of range");
        }
        catch(Exception e){
            throw new IllegalArgumentException("Could not select location: " + e.getMessage());
        }
    }

     public void sendConfirmation(Event event, User organiser, String message)throws IllegalArgumentException{
        try{
            List<EventResponse> responses = responseService.getResponses(event);

            EventResult results = resultDao.get(event.getUUID()).orElseThrow();
            
            for (var response : responses){
                User user = userService.getUserByUUID(response.getUser().toString());

                StringBuffer sb = new StringBuffer();

                StringBuffer buffer = sb.append("BEGIN:VCALENDAR\n" +
                        "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n" +
                        "VERSION:2.0\n" +
                        "METHOD:REQUEST\n" +
                        "BEGIN:VEVENT\n" +
                        "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:"+user.getEmail()+"\n" +
                        "ORGANIZER:MAILTO:"+organiser.getEmail()+"\n" +
                        "DTSTART:"+results.getDates().getSelected().get().getProperty().getStart().toEpochSecond()+"\n" +
                        "DTEND:"+ results.getDates().getSelected().get().getProperty().getEnd().toEpochSecond()+"\n" +
                        "LOCATION:"+ results.getLocations().getSelected().get().getProperty().getName()+"\n" +
                        "TRANSP:OPAQUE\n" +
                        "SEQUENCE:0\n" +
                        "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n" +
                        " 000004377FE5C37984842BF9440448399EB02\n" +
                        "DTSTAMP:20051206T120102Z\n" +
                        "CATEGORIES:Meeting\n" +
                        "DESCRIPTION:"+event.getDescription()+"\n\n" +
                        "SUMMARY:Meet request\n" +
                        "PRIORITY:5\n" +
                        "CLASS:PUBLIC\n" +
                        "BEGIN:VALARM\n" +
                        "TRIGGER:PT1440M\n" +
                        "ACTION:DISPLAY\n" +
                        "DESCRIPTION:Reminder\n" +
                        "END:VALARM\n" +
                        "END:VEVENT\n" +
                        "END:VCALENDAR");
    
                //Notification notification = Notifications.simpleMail("testing", "helloworld", "google.com");
                Notification notification = Notifications.withFile("You have been invited to: "+ event.getName(), "Message from Organiser:\n"+message +"\nEvent Description:\n"+event.getDescription(), "invite.ics", String.valueOf(buffer).getBytes(), true, "action");
                notificationService.send(notification, user);
            }
        }
        catch(Exception e){
            return;
        }


    }
}
