//-----------------------------------------------------------------
// EventResponse.java
// Let's Meet 2021
//
// Models a Users's response to an Event.

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.UUID;

//-----------------------------------------------------------------

public class EventResponse {
    //Conjugate pair
    private UUID event;
    private UUID user;

    //Member data
    private UUID conditionSet;

    public EventResponse(UUID event, UUID user, UUID conditionSet){
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

	public UUID getConditionSet() {
		return conditionSet;
	}

	public void setConditionSet(UUID conditionSet) {
		this.conditionSet = conditionSet;
	}

}
