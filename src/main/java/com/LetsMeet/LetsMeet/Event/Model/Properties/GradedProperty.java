package com.LetsMeet.LetsMeet.Event.Model.Properties;

import java.io.Serializable;

public class GradedProperty<T extends Serializable & Comparable<T>> implements Serializable {


    private static final long serialVersionUID = 1L;

    private T property;
    public int grade;
    
    public GradedProperty(T property, int grade){
        this.property = property;
        this.grade = grade;
    }

    public T getProperty() {
        return property;
    }

    public void setProperty(T property) {
        this.property = property;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
