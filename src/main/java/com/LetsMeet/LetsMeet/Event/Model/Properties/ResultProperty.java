package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultProperty<T extends Serializable & Comparable<T>> implements Serializable{


    private static final long serialVersionUID = 1L;
    
    private List<GradedProperty<T>> gradedProperties;
    private GradedProperty<T> selected;

    public ResultProperty(List<GradedProperty<T>> gradedProperties, GradedProperty<T> selected) {
        this.gradedProperties = gradedProperties;
        this.selected = selected;
    }

    public ResultProperty() {
        this(new ArrayList<>(),null);
    }

    public List<GradedProperty<T>> getGradedProperties() {
        return gradedProperties;
    }

    public void setGradedProperties(List<GradedProperty<T>> gradedProperties) {
        this.gradedProperties = gradedProperties;
    }

    public Optional<GradedProperty<T>> getSelected(){
        return Optional.ofNullable(this.selected);
    }
    
    public void setSelected(GradedProperty<T> selected) {
        this.selected = selected;
    }

    
}
