//-----------------------------------------------------------------
// EventResponse.java
// Let's Meet 2021
//
// Models a User's response to an Event.

package com.LetsMeet.LetsMeet.Event.Model;

import com.LetsMeet.LetsMeet.Event.Model.DTO.ResponseDTO;
import com.LetsMeet.LetsMeet.Event.Model.Properties.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;

import java.util.ArrayList;

//-----------------------------------------------------------------

import java.util.List;
import java.util.UUID;

//-----------------------------------------------------------------

public class EventResponse {
    //Conjugate pair
    private UUID event;
    private UUID user;

    //Member data
    private EventProperties properties;
	private ArrayList<PollResponse> poll;
	private Boolean required;

    public EventResponse(UUID event, UUID user, EventProperties properties){
		this(event, user, properties, false);
    }

	public EventResponse(UUID event, UUID user, EventProperties properties, Boolean required){
		this.event = event;
        this.user = user;
        this.properties = properties;
		this.required = required;
	}

	public EventResponse(UUID event, UUID user){
    	this(event,user,new EventProperties(new ArrayList<>(),new ArrayList<>(), new Location()));
	}

	/**
	 * Create a EventResponse from a DTO
	 * @param dto
	 * @return new EventResponse
	 */
	public static EventResponse fromDTO(ResponseDTO dto){
    	EventResponse response = new EventResponse(UUID.fromString(dto.getEventUUID()),UUID.fromString(dto.getUserUUID()));

		response.getEventProperties().setFacilities(dto.getFacilities());

		List<DateTimeRange> times = new ArrayList<>();
		for (var time : dto.getTimes())
			times.add(DateTimeRange.fromJson(time));
		response.getEventProperties().setTimes(times);

		response.getEventProperties().setLocation(new Location(dto.getLocation(), dto.getLatitude(), dto.getLongitude(), dto.getRadius()));

		return response;
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

	public EventProperties getEventProperties() {
		return properties;
	}

	public void setEventProperties(EventProperties properties) {
		this.properties = properties;
	}

	public ArrayList<PollResponse> getPoll() {
		return poll;
	}

	public void setPoll(ArrayList<PollResponse> poll) {
		this.poll = poll;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public boolean hasResponded(){
		return !this.properties.getTimes().isEmpty();
	}

}
