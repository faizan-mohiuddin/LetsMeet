package com.LetsMeet.LetsMeet.Event.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Event.Model.Constraint;
import com.LetsMeet.LetsMeet.Event.Model.Variables.*;
import com.LetsMeet.LetsMeet.Event.Model.Variable;

public interface ConditionSetServiceInterface {

    // Instantiating new ConditionSet
    public abstract ConditionSet createEmpty(); // Create new with no parameters
    public abstract ConditionSet createDefault(); // Create with default parameters

    public abstract boolean delete(UUID conditionSet);

    // Generic management
    public abstract Boolean addVariable(ConditionSet conditionSet, Variable<? extends Serializable> variable);
    public abstract Boolean addConstraint(ConditionSet conditionSet, Constraint<?> constraint);

    /* Specific helper functions */
    // TimeRange 
    public abstract void addTimeRanges(ConditionSet conditionSet, List<DateTimeRange> ranges); 
    public abstract Optional<List<DateTimeRange>> getTimeRange(ConditionSet conditionSet);

    // Services
    public abstract void setServices(ConditionSet conditionSet, List<String> services);
    public abstract List<String> getServices(UUID event);


}
