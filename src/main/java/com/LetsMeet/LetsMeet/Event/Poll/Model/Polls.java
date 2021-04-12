package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.thymeleaf.expression.Lists;

public class Polls {

    private static class pollInputParams{
        String name;
        Boolean multiselect;
        List<String> options;
    }
    
    /**
     * Creates a new poll
     * @param options each options/choice 
     * @param multiselect when true poll allows multiple options to be selected
     * @param name 
     * @return new Poll
     */
    public static Poll of(List<String> options, boolean multiselect, String name){
        Map<String, Integer> optionsMap = new HashMap<>();
        for (String s : options){
            optionsMap.put(s, 0);
        }
        return new Poll(UUID.randomUUID(), name, optionsMap, multiselect);
    }

    /**
     * Create a poll from a JSON string  
     * @param json must define "name: ", "multiselect: " and "options: {}"
     * @return new Poll
     */
    public static Poll fromJson(String json){
        Gson gson = new Gson();
        Poll poll = gson.fromJson(json, Poll.class);
        if (poll.getUUID()==null){
            poll.setUUID(UUID.randomUUID());
        }
        return poll;
    }
}
