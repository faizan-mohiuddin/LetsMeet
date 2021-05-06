package com.LetsMeet.LetsMeet.Event.Model.DTO;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

/**
 * Data Transfer Object for Event front-end communication
 */
public class EventDTO {

    private String uuid;

    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(min = 3, max = 50)
    private String location;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @NotNull
    private double radius;

    private List<String> times;

    private List<String> facilities;

    private List<String> polls;

    private MultipartFile image;

    private List<String> properties;

    @SuppressWarnings("all")
    public EventDTO(String uuid, String name, String description, String location, double latitude, double longitude,
            double radius, List<String> times, List<String> facilities, List<String> polls, MultipartFile image,
            List<String> properties) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.times = times;
        this.facilities = facilities;
        this.polls = polls;
        this.image = image;
        this.properties = properties;
        
        /*  "null"  may have been appended as workaround for zealous Spring 
        WebMvcConfigurerAdapter Formatter on single value array inputs, we can remove it now */
        this.times.remove("null");
        this.facilities.remove("null");
        this.polls.remove("null");
        this.properties.remove("null");
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getPolls() {
        return polls;
    }

    public void setPolls(List<String> polls) {
        this.polls = polls;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
    
    public void validate(){
        this.times.remove("null");
        this.facilities.remove("null");
        this.polls.remove("null");
        this.properties.remove("null");
    }
}
