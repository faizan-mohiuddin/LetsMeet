package com.LetsMeet.LetsMeet.Event.DAO;

import java.util.Collection;
import java.util.Optional;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Utilities.DAO;

import org.springframework.stereotype.Component;

@Component
public class EventDao implements DAO<Event> {

    @Override
    public Optional<Event> get(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Event> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int save(Event t) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void update(Event t) {
        // TODO Auto-generated method stub

    }

    @Override
    public String delete(Event t) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
