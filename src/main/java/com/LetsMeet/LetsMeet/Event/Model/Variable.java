//-----------------------------------------------------------------
// Variable.java
// Let's Meet 2021
//
// Let's Meet specific model class for variable as part of condition set system

package com.LetsMeet.LetsMeet.Event.Model;

import java.io.Serializable;

//-----------------------------------------------------------------

import java.util.ArrayList;
import java.util.UUID;

//-----------------------------------------------------------------

public class Variable<T extends Serializable> implements Serializable {

    UUID uuid;
    String key;
    ArrayList<T> domain;
    

    public Variable(UUID uuid, String key, T[] domain){
        this.uuid = uuid;
        this.key = key;
        this.domain = new ArrayList<T>();
        for (T e : domain){
            this.domain.add(e);
        }
    }

    public Variable(String key, T[] domain){
        this(UUID.randomUUID(),key,domain);
    }

    public Variable(T[] domain){
        this(UUID.randomUUID().toString(), domain);
    }

    // Getters
    //-----------------------------------------------------------------

    public UUID getUUID(){
        return this.uuid;
    }

    public String getKey(){
        return this.key;
    }

    public ArrayList<T> getDomain(){
        return this.domain;
    }

    // Setters
    //----------------------------------------------------------------
    public void append(T domainValue){
        this.domain.add(domainValue);
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDomain(ArrayList<T> domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "Variable [domain=" + domain + ", key=" + key + ", uuid=" + uuid + "]";
    }

}