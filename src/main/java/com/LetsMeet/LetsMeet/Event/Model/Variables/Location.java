package com.LetsMeet.LetsMeet.Event.Model.Variables;

import java.io.Serializable;

public class Location implements Serializable {
    
    // Is Serializable so declare version for backwards compatibility (4145802332762387198L)
    private static final long serialVersionUID = 4145802332762387198L;
    private String name;
    private double longitude;
    private double latitude;
    private double radius;

    public Location(String name, double latitude, double Longitude, double radius){
        this.name = name;
        this.latitude = latitude;
        this.longitude = Longitude;
        this.radius = radius;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
