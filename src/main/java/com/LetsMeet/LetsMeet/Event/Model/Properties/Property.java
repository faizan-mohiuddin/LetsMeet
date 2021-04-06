package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;

public class Property implements Serializable, Comparable<Property> {

    /**
     *
     */
    private static final long serialVersionUID = 1420053917484048655L;

    private String name;

    

    @Override
    public int compareTo(Property o) {
        return 0;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
