package com.LetsMeet.LetsMeet.Event.Service;

import java.util.ArrayList;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.Poll;

public class PollService {

    public Poll create(String name, ArrayList<String> options){

        return new Poll(UUID.randomUUID(), name, options);

    }
    
}
