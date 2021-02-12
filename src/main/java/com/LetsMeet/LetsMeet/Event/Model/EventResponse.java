//-----------------------------------------------------------------
// EventResponse.java
// Let's Meet 2021
//
// Models a User's response to an Event.

package com.LetsMeet.LetsMeet.Event.Model;

import java.util.ArrayList;

//-----------------------------------------------------------------

import java.util.UUID;

//-----------------------------------------------------------------

public class EventResponse {
    //Conjugate pair
    private UUID event;
    private UUID user;

    //Member data
    ConditionSet conditionSet;
	ArrayList<PollResponse> poll;

    public EventResponse(UUID event, UUID user, ConditionSet conditionSet){
        this.event = event;
        this.user = user;
        this.conditionSet = conditionSet;
    }

	public UUID getEvent() {
		return event;
	}

	public void setEvent(UUID event) {
		this.event = event;
	}

	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}

	public ConditionSet getConditionSet() {
		return conditionSet;
	}

	public void setConditionSet(ConditionSet conditionSet) {
		this.conditionSet = conditionSet;
	}

}
