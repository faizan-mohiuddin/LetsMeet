package com.LetsMeet.LetsMeet.Event.Model.DTO;

import com.LetsMeet.LetsMeet.Event.Model.EventResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Response front-end communication
 */
public class ResponseDTO {

    private String eventUUID;
    private String userUUID;
    private String location;
    private double latitude;
    private double longitude;
    private double radius;
    private List<String> times;
    private List<String> facilities;
    private List<String> pollResponse;

    @SuppressWarnings("all")
    public ResponseDTO(String eventUUID, String userUUID, String location, double latitude, double longitude, double radius, List<String> times, List<String> facilities, List<String> pollResponse) {
        this.eventUUID = eventUUID;
        this.userUUID = userUUID;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.times = times;
        this.facilities = facilities;
        this.pollResponse = pollResponse;

        validate();
    }

    public void validate(){
        this.times.remove("null");
        this.facilities.remove("null");
        this.pollResponse.remove("null");
    }

    public static ResponseDTO fromResponse(EventResponse eventResponse){

        return new ResponseDTO(eventResponse.getEvent().toString(),
                eventResponse.getUser().toString(),
                eventResponse.getEventProperties().getLocation().getName(),
                eventResponse.getEventProperties().getLocation().getLatitude(),
                eventResponse.getEventProperties().getLocation().getLongitude(),
                eventResponse.getEventProperties().getLocation().getRadius(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public String getEventUUID() {
        return eventUUID;
    }

    public void setEventUUID(String eventUUID) {
        this.eventUUID = eventUUID;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<String> getPollResponse() {
        return pollResponse;
    }

    public void setPollResponse(List<String> pollResponse) {
        this.pollResponse = pollResponse;
    }
}
