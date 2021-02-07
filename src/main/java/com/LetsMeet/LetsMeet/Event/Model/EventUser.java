//-----------------------------------------------------------------
// EventUser.java
// Let's Meet 2021
//
// Impliments dynamic loading between Event and User objects
// If a class A is requested but not in storage, this calls DAO to construct A

package com.LetsMeet.LetsMeet.Event.Model;

import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.DAO.EventDao;
import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;

public class EventUser {

    @Autowired
    EventDao eventDao;

    @Autowired
    UserDao userDao;

    private UUID eventUUID;
    private Optional<Event> event;
    private UUID userUUID;
    private Optional<User> user;

    // Constructors to allow for each possible permutation
    //-----------------------------------------------------------------

    public EventUser(Event event, User user){
        this.eventUUID = event.getUUID();
        this.event = Optional.of(event);
        this.userUUID = user.getUUID();
        this.user = Optional.of(user);
    }

    public EventUser(UUID event, UUID user){
        this.eventUUID = event;
        this.event = Optional.empty();
        this.userUUID = user;
        this.user = Optional.empty();
    }

    public EventUser(Event event,UUID user ){
        this.eventUUID = event.getUUID();
        this.event = Optional.of(event);
        this.userUUID = user;
        this.user = Optional.empty();
    }

    public EventUser(UUID event, User user){
        this.eventUUID = event;
        this.event = Optional.empty();
        this.userUUID = user.getUUID();
        this.user = Optional.of(user);
    }

    //-----------------------------------------------------------------

    public Optional<Event> getEvent(){
        if (this.event.isPresent()){return this.event;}
        else {
            this.event = eventDao.get(this.eventUUID);
            return this.event;
        }
    }

    public Optional<User> getUser(){
        if (this.user.isPresent()){return this.user;}
        else{
            this.user = userDao.get(this.userUUID);
            return this.user;
        }

    }
}


