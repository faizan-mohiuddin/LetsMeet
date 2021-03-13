package com.LetsMeet.LetsMeet.Business.Venue.Model;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.google.gson.Gson;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Venue {
    UUID venueUUID;
    String name;
    List<String> facilities = new ArrayList<>();
    public Business business;
    String address;
    boolean coords;
    double longitude;
    double latitude;

    public Venue(UUID uuid, String name){
        this.venueUUID = uuid;
        this.name = name;
    }

    public Venue(String uuid, String name){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;
    }

    public Venue(UUID uuid, String name, List<String> facilities){
        this.venueUUID = uuid;
        this.name = name;
        this.facilities = facilities;
    }

    public Venue(String uuid, String name, String facilities){
        this.venueUUID = UUID.fromString(uuid);
        this.name = name;

        // Check if facilities is null
        if(!(facilities == null)){
            // Populate facilities
            try {
                JSONArray obj = new JSONArray(facilities);
                for (int i = 0; i < obj.length(); i++) {
                    this.facilities.add(obj.get(i).toString());
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
        this.address = address;

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
                JSONArray obj = new JSONArray(facilities);
                for (int i = 0; i < obj.length(); i++) {
                    this.facilities.add(obj.get(i).toString());
                }
            } catch (Exception e) {
                System.out.println("Venue : Init(String, String, String)");
                System.out.println(e);
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
}
