package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.List;
import java.util.UUID;

public class Poll {
    
    UUID uuid;
    List<String> options;
    List<PollResponse> responses;
    Boolean selectMultiple;
}
