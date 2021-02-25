package com.LetsMeet.LetsMeet.Event.Model.Variables;

import java.io.Serializable;

public class Location implements Serializable {
    
    private double longitude;
    private double latitude;
    private double radius;

    public Location(double latitude, double Longitude, double radius){
        this.latitude = latitude;
        this.longitude = Longitude;
        this.radius = radius;
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
