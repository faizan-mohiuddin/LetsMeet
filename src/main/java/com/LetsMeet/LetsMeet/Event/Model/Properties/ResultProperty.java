package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultProperty<T extends Serializable & Comparable<T>> implements Serializable{


    private static final long serialVersionUID = 1L;
    
    private List<GradedProperty<T>> gradedProperties;
    private int chosen;
    public ResultProperty(List<GradedProperty<T>> gradedProperties, int chosen) {
        this.gradedProperties = gradedProperties;
        this.chosen = chosen;
    }

    public ResultProperty() {
        this(new ArrayList<>(),-1);
    }

    public List<GradedProperty<T>> getGradedProperties() {
        return gradedProperties;
    }
    public void setGradedProperties(List<GradedProperty<T>> gradedProperties) {
        this.gradedProperties = gradedProperties;
    }
    public int getChosen() {
        return chosen;
    }
    public void setChosen(int chosen) {
        this.chosen = chosen;
    }

    
}
