package com.LetsMeet.LetsMeet.Event.Model.DTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class EventDTO {

    //@Size(min = 3, max = 50)
    private String name;

    //@Size(max = 500)
    private String description;

    //@Size(min = 3, max = 50)
    private String location;

    //@NotNull
    private double latitude;

    //@NotNull
    private double longitude;

    //@NotNull
    private double radius;

    //@NotEmpty // TODO validate that length is not odd
    private String[] times;

    private MultipartFile image;

    public EventDTO(@Size(min = 3, max = 50) String name, @Size(max = 500) String description,
            @Size(min = 3, max = 50) String location, @NotEmpty double latitude, @NotEmpty double longitude,
            @NotEmpty double radius, @NotEmpty String[] times, MultipartFile image) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.times = times;
        this.image = image;
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

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    
}
