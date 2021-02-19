package com.LetsMeet.LetsMeet.Event.Service;

import java.util.ArrayList;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Model.Poll;

public class PollService {

    public Poll create(String name, UUID event, ArrayList<String> options, Boolean multiselect){

        return new Poll(UUID.randomUUID(), event, name, options);

    }
    
}
