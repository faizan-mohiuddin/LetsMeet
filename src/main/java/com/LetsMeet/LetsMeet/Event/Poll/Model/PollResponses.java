package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollResponses {

    private PollResponses() {
        throw new IllegalStateException("Utility class");
    }
    
    
    public static PollResponse forPoll(Poll poll){

        Map<String, Boolean> responses = new HashMap<>();
        for (var x : poll.keySet())
            responses.put(x, false);

        return new PollResponse(responses);
    }

    public static PollResponse forPoll(Poll poll, List<String> selected){
        PollResponse response = forPoll(poll);

        for (String s : selected){
            response.setOption(s, true);
        }

        return response;
    }    
}
