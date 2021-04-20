//-----------------------------------------------------------------
// EventService.java
// Let's Meet 2021
//
// Implements the operations possible on an event.

package com.LetsMeet.LetsMeet.Event.Service;

//-----------------------------------------------------------------

import java.io.IOException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import com.LetsMeet.LetsMeet.Event.Model.*;
import com.LetsMeet.LetsMeet.Root.Notification.NotificationService;
import com.LetsMeet.LetsMeet.Root.Notification.Notifications;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPermissionDao;
import com.LetsMeet.LetsMeet.Event.DAO.EventPollDAO;
import com.LetsMeet.LetsMeet.Event.DAO.EventResultDao;
import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;
import com.LetsMeet.LetsMeet.Event.Poll.PollService;
import com.LetsMeet.LetsMeet.Event.Poll.Model.Poll;
import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetTuple;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.LetsMeet.LetsMeet.Utilities.MethodService.deepCopyStringList;

//-----------------------------------------------------------------

@Service
public class EventService{

    // Logger
    private static final Logger LOGGER=LoggerFactory.getLogger(EventService.class);

    // Components
    @Autowired
    EventDao eventDao;

    @Autowired
    EventPermissionDao permissionDao;

    @Autowired
    UserService userService;

    @Autowired
    EventResponseService responseService;

    @Autowired
    EventResultDao resultDao;

    @Autowired
    private MediaService mediaService;

    @Autowired
    PollService pollService;

    @Autowired
    EventPollDAO eventPollDAO;

    @Autowired
    EventResultService resultService;

    @Autowired
    NotificationService notificationService;


    /* -- CRUD operations -- */

    // Create a new event:
    // Creates a new event and sets the given user (typically the one creating the event) administrative rights over it.

    public Event save(Event event, User user) throws IOException{

        try{
            eventDao.save(event);

            // Record user as owner of the event by creating a new EventPermission Record
            permissionDao.save(EventPermission.fromEntity(event, user, true));

            return event;
        }
        catch(Exception e){
            throw new IOException("Unable to create new event");
        }
    }


    /**
     * Sends Event to persistent layer and overwrites existing data
     * @param user The user performing the update (must have edit privileges)
     * @param event The event to be updated
     * @return true if update is successful
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public boolean update(User user, Event event) throws IllegalArgumentException, IOException{

        if(!this.checkOwner(event.getUUID(), user.getUUID())) {
            throw new IllegalArgumentException("Provided User does not have sufficient privileges. User= <" + user.getUUID() + ">");
        }

        return eventDao.update(event);

    }
    /**
     * Updates event with the contents of an EventDTO and sends to persistence layer
     * @param user The user performing the update (must have edit privileges)
     * @param event The event to be updated
     * @param eventDTO The data to update the event with
     * @return true if update is successful
     */
    public boolean update(User user, Event event, @Valid EventDTO eventDTO){
        try{
            // Update the basics (name, description, image, generic properties)
            event.setName(eventDTO.getName());
            event.setDescription(eventDTO.getDescription());

            // Update image
            if (eventDTO.getImage().getSize()>0){
                this.setHeaderImage(event, eventDTO.getImage());
            }

            /*  Update EventProperties  */
            // Location
            event.getEventProperties().setLocation(new Location(
                eventDTO.getLocation(),
                eventDTO.getLatitude(),
                eventDTO.getLongitude(),
                eventDTO.getRadius()));

            // Times
            List<DateTimeRange> times = new ArrayList<>();
            for (var time : eventDTO.getTimes())
                times.add(DateTimeRange.fromJson(time));

            event.getEventProperties().setTimes(times);

            // Services
            event.getEventProperties().setFacilities(eventDTO.getFacilities());

            // Send modified event to update
            return update(user, event);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    // Delete an existing event
    // Checks if given user has edit privileges first. Also deletes Events ConditionSet.
    /**
     * Requests the persistent layer to remove the supplied Event
     * @param event Event to be deleted
     * @param user User requesting deletion (must have permission)
     * @return true if deletion reported successfully
     * @throws IllegalArgumentException
     */
    public Boolean deleteEvent(Event event, User user) throws IllegalArgumentException{

        try{
            if(!this.checkOwner(event.getUUID(), user.getUUID())) {
                throw new IllegalArgumentException("Provided User does not have sufficient privileges. User= <" + user.getUUID() + ">");
            }

            // Get event users
            List<User> records = this.EventsUsers(event.getUUID());
            Boolean result = eventDao.delete(event);

            if(result){
                // Remove one by one
                Collection<Event> userEvents;
                for(User r : records) {
                    if(r.getIsGuest()) {
                        // If an affected Guest no longer has any other events, delete guest account
                        userEvents = this.getUserEvents(r);
                        if (userEvents.size() == 0) {
                            // Delete guest account
                            String response = userService.deleteUser(user);
                            System.out.println(response);
                        }
                    }
                }
            }

             return result;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Unable to delete Event <" + event.getUUID() + ">" + e.getMessage());
        }
    }

    /**
     * Get a list of all the event.
     * Expensive, avoid this and only request events you need.
     * @return
     */
    public Collection<Event> getEvents() {
        try{
            return eventDao.getAll().get();
        }
        catch(Exception e){
            LOGGER.error("Could not get all events: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // Returns general search results
    public List<Event> search(String term){
        String likeTerm = "'%" + String.format("%s", term) + "%'";
        String query = String.format("SELECT * FROM Event WHERE Event.EventUUID = '%s' OR " +
                        "Event.Name LIKE %s OR Event.Description LIKE %s OR Event.Location LIKE %s",
                term, likeTerm, likeTerm, likeTerm);
        Optional<List<Event>> events = eventDao.search(query);
        if(events.isPresent()){
            return events.get();
        }
        return null;
    }

    /**
     * Requests the event from persistent layer
     * @param eventUUID ID of desired event
     * @return The event
     * @throws IllegalArgumentException
     * @depreciated Use .get() method
     */
    @Deprecated(forRemoval = true)
    public Event getEvent(String eventUUID) throws IllegalArgumentException {
        try{
            Optional<Event> event = eventDao.get(eventUUID);

            if(event.isPresent()){return event.get();}
            else throw new IllegalArgumentException("No Event found for UUID <" + eventUUID + ">");

        }
        catch (IOException e){
            throw new IllegalArgumentException("No Event found for UUID <" + eventUUID + ">");
        }
    }

    /**
     * Requests the event from persistent layer
     * @param eventUUID ID of desired event
     * @return The event
     * @throws IllegalArgumentException
     */
    public Optional<Event> get(UUID eventUUID){
        try{
            return eventDao.get(eventUUID);
        }catch(Exception e){
            throw new IllegalArgumentException("Error:" + e.getMessage());
        }
    }

    /**
     * Adds an existing poll to an event
     * @param event
     * @param poll
     * @return
     * @throws IllegalArgumentException if either event or poll are not found in persistence
     */
    public boolean addPoll(Event event, Poll poll) throws IllegalArgumentException{
        try {
            eventPollDAO.save(new LetsMeetTuple<>(event.getUUID(), poll));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not add Poll<" + poll.getUUID() + "> to Event<" + event.getUUID() + "> : " + e.getMessage());
        }
        return false;
    }

    public boolean removePoll(Event event, Poll poll){

        try {
            eventPollDAO.delete(new LetsMeetTuple<>(event.getUUID(), poll));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not remove Poll<" + poll.getUUID() + "> from Event<" + event.getUUID() + "> : " + e.getMessage());
        }
        return false;
    }

    public List<Poll> getPolls(Event event) throws IllegalArgumentException{
        try {
            return eventPollDAO.get(event.getUUID()).orElseThrow();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not get Polls for Event<"+event.getUUID()+"> : " + e.getMessage());
        }
    }

    @Deprecated(forRemoval = true)
    public void setProperty(Event event, String key, String value) throws IOException{
        event.getProperties().set(key, value);
        eventDao.update(event);
    }

    @Deprecated(forRemoval = true)
    public String getProperty(Event event, String key){
        return event.getProperties().get(key);
    }


    //-----------------------------------------------------------------
    /* -- User related operations --

        Users are linked to an event either through an EventPermission, which records whether they own an event and can edit it
        or through a EventResponse which records their "response" to an event and is managed in a separate ResponseManager.java
    */

    // Returns all events which given user is owner of
    public Collection<Event> getUserEvents(User user) {
        List<Event> events = new ArrayList<>();

        try{
            // Get list of users permissions
            List<EventPermission> eventPerms = permissionDao.get(user.getUUID().toString()).get();
            var perms = permissionDao.getWithEvent(user.getUUID());

            return new ArrayList<>(perms.values());
        }
        catch (Exception e){
            throw new IllegalArgumentException("No Event found for UUID <" + user.getUUID() + ">");
        }
    }

    // Set the boolean isOwner for a user/event pair
    public Boolean setPermissions(Event event, User user, Boolean owner) {
        try{
            permissionDao.save(new EventPermission(event.getUUID(), user.getUUID(), owner));
            return true;
        }
        catch (Exception e){
            LOGGER.error("Could not set event permissions {}", e.getMessage());
            return false;
        }
    }

    // Returns true if given User owns event, otherwise false.
    public boolean checkOwner(UUID eventUUID, UUID userUUID) {
        Optional<EventPermission> response = permissionDao.get(eventUUID, userUUID);

        if (response.isPresent()) {
            return response.get().getIsOwner();
        }
        return false;
    }

    // Returns list of users who have responded to an event
    public List<User> EventsUsers(UUID eventUUID){
        List<User> users = new ArrayList<>();

        // Get list of users permissions
        List<EventPermission> eventPerms = permissionDao.get(eventUUID.toString()).get();

        // Get each user on permissions list
        for(EventPermission e : eventPerms){
            users.add(userService.getUserByUUID(e.getUser().toString()));
        }
        return users;
    }

    //-----------------------------------------------------------------
    /* -- ConditionSet related operations --

        Each Event has it's own ConditionSet. This is used to record any data which logically belongs to an event
        such as the range of dates it could take place. Dates that a user could make it to an event belong to that user
        not the event and as such should be stored within their Response and not, NOT, in the events ConditionSet.
        Constraints that a User must be at the event are stored here.

        This section is mostly a gateway to the separate ConditionSet manager, which handles the heavy logic
        Here, get the Event, then call the ConditionSetService with the UUID of the ConditionSet attached to the event.
    */


    // Will add a new period to the time range constraint - use to set the periods that the event could take place.
    public boolean setTimeRange(Event event, List<DateTimeRange> times) {
        try{
            event.getEventProperties().setTimes(times);
            return eventDao.update(event);
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    public List<DateTimeRange> getTimeRange(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getTimes();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public static List<DateTimeRange> processJsonRanges(String tRanges){
        List<DateTimeRange> ranges = new ArrayList<>();
        Gson g = new Gson();
        JsonObject[] obj = g.fromJson(tRanges, JsonObject[].class);
        for (int i = 0; i < obj.length; i++) {
            String s = obj[i].get("start").getAsString();
            String e = obj[i].get("end").getAsString();

            s = checkTimeRangeForParsing(s);
            e = checkTimeRangeForParsing(e);

            var start = ZonedDateTime.parse(s);
            var end = ZonedDateTime.parse(e);
            ranges.add(new DateTimeRange(start, end));
        }
        return ranges;
    }

    public boolean setFacilities(Event event, List<String> facilities) {
        try{
            event.getEventProperties().setFacilities(facilities);
            return true;
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public List<String> getFacilities(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getFacilities();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public boolean setLocation(Event event, Location location) {
        try{
            event.getEventProperties().setLocation(location);
            return true;
        }
        catch(Exception e){
            LOGGER.error("Could not set time range: {}", e.getMessage());
            return false;
        }
    }

    public Location getLocation(UUID eventUUID) {
        try{
            Event event = eventDao.get(eventUUID).get();
            return event.getEventProperties().getLocation();
        }
        catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    public List<List<String>> processTimeRanges(Event event){
        List<List<String>> strtimes = new ArrayList<>();
        List<String> arr = new ArrayList<>();
        int rows = -1;
        for(DateTimeRange t : event.getEventProperties().getTimes()){
            rows += 1;
            arr.clear();
            // Start date
            ZonedDateTime s = t.getStart();
            arr.add(String.format("%s-%s-%s",s.getYear(), s.getMonthValue(), s.getDayOfMonth()));

            // Start time
            String h = processTime(s.getHour());
            String m = processTime(s.getMinute());
            String sec = processTime(s.getSecond());

            arr.add(String.format("%s:%s:%s", h, m, sec));

            // End date
            ZonedDateTime e = t.getEnd();
            arr.add(String.format("%s-%s-%s",e.getYear(), e.getMonthValue(), e.getDayOfMonth()));

            // End time
            h = processTime(e.getHour());
            m = processTime(e.getMinute());
            sec = processTime(e.getSecond());

            arr.add(String.format("%s:%s:%s", h, m, sec));

            // Add input ID's
            arr.add(String.format("startDay%d", rows));
            arr.add(String.format("startTime%d", rows));
            arr.add(String.format("endDay%d", rows));
            arr.add(String.format("endTime%d", rows));

            strtimes.add(deepCopyStringList(arr));
        }
        return strtimes;
    }

    private static String processTime(int t){
        String st;
        if(t < 10){
            st = String.format("0%s", t);
        }else{
            st = Integer.toString(t);
        }
        return st;
    }

    private static String checkTimeRangeForParsing(String t){
        // Check parse-ability of string
        // Split on T
        String[] parts = t.split("T");
        String[] Date = parts[0].split("-");
        int counter = 0;
        for(String d : Date){
            if(d.length() < 2){
                Date[counter] = "0" + Date[counter];
            }
            counter += 1;
        }
        // Reconstruct String
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Date[0]);
        stringBuilder.append("-");
        stringBuilder.append(Date[1]);
        stringBuilder.append("-");
        stringBuilder.append(Date[2]);
        stringBuilder.append("T");
        stringBuilder.append(parts[1]);
        return stringBuilder.toString();
    }

    /**
     * Uploads image and sets it as the supplied events "header_image"
     * @param event event to be modified
     * @param file must be image file
     */
    public void setHeaderImage(Event event, MultipartFile file) throws IllegalArgumentException{
        try{
            // Ensure file is of image type
            if (ImageIO.read(file.getInputStream()) == null){
                throw new IllegalArgumentException("Invalid file: Must be image");
            }
            // Upload image
            var headerImage = mediaService.newMedia(file, "event", "banner").orElseThrow();
            // Set properties
            event.getProperties().set("header_image", mediaService.generateURL(headerImage));

        }catch (IOException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    public void inviteGuestToEvent(User user, Event event){
        // Check if guest has been invited to event already
        Optional<EventResponse> checker = responseService.getResponse(user, event);

        if(checker.isEmpty()) {
            // If not - invite (Add to isGuest table)
            EventResponse response = new EventResponse(event.getUUID(), user.getUUID());
            responseService.saveResponse(response);
        }
    }

    /**
     * Send Event invites to users. If identifier can't resolved to a registered user
     * then a guest account is created and invited.
     * @param event to be invited to
     * @param identifiers of users/entities to be invited
     * @throws IllegalArgumentException if an identifier is invalid
     */
    public void invite( Event event, List<String> identifiers) throws IllegalArgumentException, MailAuthenticationException {
        for ( String identifier : identifiers){
            User user;

            if (identifier.contains("@") && identifier.contains("."))
                user = userService.getUserByEmail(identifier);

            else
                user = userService.getUserByUUID(identifier);

            if (user == null) user = userService.createGuest(identifier, event);

            responseService.createResponse(user,event,false);

            // If user is guest they needs the appropriate link
            if(user.getIsGuest()){
                String guestLink = String.format("localhost:8080/event/%s/respond/%s", event.getUUID().toString(), user.getUUID().toString());
                LOGGER.info(guestLink);
                notificationService.send(Notifications.simpleMail("You have been invited to an Event",
                        "You have been invited to " + event.getName(), guestLink), user);
            }else {
                notificationService.send(Notifications.simpleMail("", "", ""), user);
            }
        }
    }
}
