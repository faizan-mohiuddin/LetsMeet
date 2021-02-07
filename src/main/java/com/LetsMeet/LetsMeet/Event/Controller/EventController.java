package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    @Autowired
    EventService eventServiceInterface;

    @RequestMapping(value = "/api/v1/event/all")
    public ResponseEntity<Object> getEvents(){
        return new ResponseEntity<>(eventServiceInterface.getEvents(),HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/event/{uuid}")
    public ResponseEntity<Object> updateEvent(@PathVariable("uuid") String uuid, @RequestBody Event event){
        eventServiceInterface.updateEvent(uuid, event);
        return new ResponseEntity<>("Event updated succesfully",HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/event/{uuid}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("uuid") String uuid){
        eventServiceInterface.deleteEvent(uuid);
        return new ResponseEntity<>("User deleted succesfully",HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/event")
    public ResponseEntity<Object> createEvent(@RequestBody Event event){
        eventServiceInterface.createEvent(event);

        return new ResponseEntity<>("User created succesfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/event/user")
    public ResponseEntity<Object> getUserEvents(@PathVariable("uuid") String uuid){
        return new ResponseEntity<>(eventServiceInterface.getUserEvents(uuid), HttpStatus.OK);
    }

    @PostMapping (value = "/api/v1/event/user")
    public ResponseEntity<Object> addUser(@PathVariable("eventuuid") String eventUuid, @PathVariable("useruuid") String userUuid){
        eventServiceInterface.setPermissions(eventUuid, userUuid, true);
        return new ResponseEntity<>("User added succesfully", HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/event/user")
    public ResponseEntity<Object> removeUser(@PathVariable("eventuuid") String eventUuid, @PathVariable("useruuid") String userUuid){
        eventServiceInterface.setPermissions(eventUuid, userUuid, false);
        return new ResponseEntity<>("User removed succesfully", HttpStatus.OK);
    }


}
