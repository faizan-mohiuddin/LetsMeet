//-----------------------------------------------------------------
// Variable.java
// Let's Meet 2021
//
// Let's Meet specific model class for variable as part of condition set system

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.ArrayList;
import java.util.UUID;

//-----------------------------------------------------------------

public class Variable<T> {

    UUID uuid;
    String name;
    ArrayList<T> domain;
    

    public Variable(String uuid, String name, T[] domain){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.domain = new ArrayList<T>();
        for (T e : domain){
            this.domain.add(e);
        }
    }

    // Getters
    //-----------------------------------------------------------------

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<T> getDomain(){
        return this.domain;
    }

    // Setters
    //----------------------------------------------------------------
    public void append(T domainValue){
        this.domain.add(domainValue);
    }

}