//-----------------------------------------------------------------
// EventPermissionDao.java
// Let's Meet 2021
//
// Responsible for performing CRUD operations on EventResponse objects
package com.LetsMeet.LetsMeet.Event.DAO;

//-----------------------------------------------------------------

import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Utilities.DAO;

//-----------------------------------------------------------------

public class EventResponseDao implements DAO<EventResponse> {

    // Get
    //-----------------------------------------------------------------    
    @Override
    public Optional get(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    // Create
    //-----------------------------------------------------------------
    @Override
    public Boolean save(EventResponse t) {
        // TODO Auto-generated method stub
        return false;
    }


    // Update
    //-----------------------------------------------------------------

    @Override
    public Boolean update(EventResponse t) {
        // TODO Auto-generated method stub
        return false;
    }

    // Delete
    //-----------------------------------------------------------------

    @Override
    public Boolean delete(EventResponse t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(UUID uuid) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
