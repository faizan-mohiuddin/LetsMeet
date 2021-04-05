package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Polls {
    
    public static Poll of(List<String> options, boolean multiselect, String name){
        Map<String, Integer> optionsMap = new HashMap<>();
        for (String s : options){
            optionsMap.put(s, 0);
        }
        return new Poll(UUID.randomUUID(), name, optionsMap, multiselect);
    }
}
