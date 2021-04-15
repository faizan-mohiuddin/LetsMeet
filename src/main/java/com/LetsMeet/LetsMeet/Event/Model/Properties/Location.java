package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;

public class Location implements Serializable, Comparable<Location> {
    
    // Is Serializable so declare version for backwards compatibility (4145802332762387198L)
    private static final long serialVersionUID = 4145802332762387198L;
    private String name;
    private double longitude;
    private double latitude;
    private double radius;

    static final double RADIUS_MAJOR = 6378137.0;
    static final double RADIUS_MINOR = 6356752.3142;

    public Location(String name, double latitude, double Longitude, double radius){
        this.name = name;
        this.latitude = latitude;
        this.longitude = Longitude;
        this.radius = radius;
    }

    public Location(){
        this("none",0d,0d,0d);
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

    public double projectX() {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(this.longitude) / 2)) * RADIUS_MAJOR;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double projectY() {
        return Math.toRadians(this.latitude) * RADIUS_MAJOR;
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

    @Override
    public int compareTo(Location o) {
        // cX is point of y-axis intersection on line which passes through location X at 45 degrees (gradient of 1)
        double cA = this.longitude + this.latitude;
        double cB = o.longitude + o.latitude;
        if (cB == cA) return 0;             // Intersect y-axis at same point, thus are neither before nor after
        else return (cB < cA) ? 1 : -1;     // Intersect y-axis below = before, or above = after
    }
}
