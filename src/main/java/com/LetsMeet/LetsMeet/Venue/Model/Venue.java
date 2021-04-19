package com.LetsMeet.LetsMeet.Venue.Model;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.google.gson.Gson;

import java.util.*;

public class Venue {
    UUID venueUUID;
    String name;
    List<String> facilities = new ArrayList<>();
    public Business business;
    String address;
    boolean coords;
    double longitude;
    double latitude;
    VenueOpenTimes openTimes = new VenueOpenTimes();

    public Venue(UUID uuid, String name){
        this.venueUUID = uuid;
        this.name = name;
        this.openTimes.setVenueUUID(this.venueUUID);
    }

    public Venue(String uuid, String name){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;
        this.openTimes.setVenueUUID(this.venueUUID);
    }

    public Venue(UUID uuid, String name, List<String> facilities){
        this.venueUUID = uuid;
        this.name = name;
        this.facilities = facilities;
        this.facilities.removeAll(Collections.singleton(""));
        this.openTimes.setVenueUUID(this.venueUUID);
    }

    public Venue(String uuid, String name, String facilities){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;
        this.openTimes.setVenueUUID(this.venueUUID);

        // Check if facilities is null
        if(!(facilities == null)){
            // Populate facilities
            try {
                Gson gson = new Gson();
                String[] obj = gson.fromJson(facilities, String[].class);
                for (int i = 0; i < obj.length; i++) {
                    String fac = obj[i].toString();
                    if(!fac.equals("")) {
                        this.facilities.add(fac);
                    }
                }
            } catch (Exception e) {
                System.out.println("Venue : Init(String, String, String)");
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    public Venue(UUID uuid, String name, List<String> facilities, String address, String longitude, String latitude){
        this.venueUUID = uuid;
        this.name = name;
        this.facilities = facilities;
        this.facilities.removeAll(Collections.singleton(""));
        this.address = address;
        this.openTimes.setVenueUUID(this.venueUUID);

        if(longitude == null || latitude == null) {
            this.coords = false;
        }else{
            this.longitude = Double.parseDouble(longitude);
            this.latitude = Double.parseDouble(latitude);
            this.coords = true;
        }
    }

    public Venue(String uuid, String name, String facilities, String address, String longitude, String latitude){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;
        this.address = address;
        this.openTimes.setVenueUUID(this.venueUUID);

        if(longitude == null || latitude == null) {
            this.coords = false;
        }else{
            this.longitude = Double.parseDouble(longitude);
            this.latitude = Double.parseDouble(latitude);
            this.coords = true;
        }

        if(!(facilities == null)){
            // Populate facilities
            try {
                Gson gson = new Gson();
                String[] obj = gson.fromJson(facilities, String[].class);
                for (int i = 0; i < obj.length; i++) {
                    String fac = obj[i].toString();
                    if(!fac.equals("")) {
                        this.facilities.add(fac);
                    }
                }
            } catch (Exception e) {
                System.out.println("Venue : Init(String, String, String)");
                System.out.println(facilities);
                e.printStackTrace();
            }
        }
    }

    public UUID getUUID(){
        return this.venueUUID;
    }

    public String getName(){
        return this.name;
    }

    public void addFacility(String f){
        this.facilities.add(f);
    }

    public String getJsonFacilities(){
        String json = new Gson().toJson(this.facilities);
        return json;
    }

    public void setName(String name){
        if(!name.equals("")){
            this.name = name;
        }
    }

    public void setFacilities(String facilities){
        if(!facilities.equals("")){
            this.facilities = new ArrayList<>(Arrays.asList(facilities.split(",")));
            this.facilities.removeAll(Collections.singletonList(""));
        }
    }

    public void setLocation(String location, String latitude, String longitude){

        if(!location.equals("")){
            // Set values
            this.address = location;
            this.coords = false;
            this.longitude = 0;
            this.latitude = 0;
        }

        if((!latitude.equals("") && !(latitude == null)) || (!longitude.equals("") && !(longitude == null))){
            this.latitude = Double.parseDouble(latitude);
            this.longitude = Double.parseDouble(longitude);
            this.coords = true;
        }
    }

    public VenueSanitised convertToSanitised(){
        return new VenueSanitised(this.name, this.facilities);
    }

    public Business getBusiness(){
        return this.business;
    }

    public void setBusiness(Business b){
        this.business = b;
    }

    public int numFacilities(){
        return this.facilities.size();
    }

    public List<String> getFacilities(){
        return this.facilities;
    }

    public String getAddress(){
        return this.address;
    }

    public Double getLongitude(){
        if(this.coords) {
            return this.longitude;
        }else{
            return null;
        }
    }

    public Double getLatitude(){
        if(this.coords) {
            return this.latitude;
        }else{
            return null;
        }
    }

    public Boolean getCoords(){
        return this.coords;
    }

    public void removeFacility(String facility){
        this.facilities.remove(facility);
    }

    public VenueOpenTimes getOpenTimes(){
        return this.openTimes;
    }

    public void setOpenTimes(VenueOpenTimes t){
        this.openTimes = t;
    }

    public static Boolean CompareDay(String a, String b){
        if(a.equals(b)){
            return true;
        }
        return false;
    }
}
