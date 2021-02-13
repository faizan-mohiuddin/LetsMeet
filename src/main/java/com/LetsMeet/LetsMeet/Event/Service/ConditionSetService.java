package com.LetsMeet.LetsMeet.Event.Service;

import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;

import org.springframework.stereotype.Service;

@Service
public class ConditionSetService {

    public ConditionSet create(){
        return new ConditionSet(UUID.randomUUID().toString());
    }
    
}
