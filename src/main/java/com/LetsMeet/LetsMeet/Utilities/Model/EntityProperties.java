package com.LetsMeet.LetsMeet.Utilities.Model;

import java.util.HashMap;

public class EntityProperties {
    HashMap<String, String> properties;

    public EntityProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public EntityProperties(){
        this(new HashMap<>());
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public String get(String key){
        return this.properties.get(key);
    }

    public void set(String key, String value){
        this.properties.put(key, value);
    }
}
