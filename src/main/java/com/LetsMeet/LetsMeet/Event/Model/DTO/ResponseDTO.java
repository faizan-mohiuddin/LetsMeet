package com.LetsMeet.LetsMeet.Event.Model.DTO;

import java.util.List;

public class ResponseDTO {

    private String uuid;
    private String location;
    private double lattitude;
    private double longitude;
    private double radius;
    private List<String> times;
    private List<String> facilities;
    private List<String> pollResponse;

    public ResponseDTO(String uuid, String location, double latitude, double longitude, double radius, List<String> times, List<String> facilities, List<String> pollResponse) {
        this.uuid = uuid;
        this.location = location;
        this.lattitude = latitude;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
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
