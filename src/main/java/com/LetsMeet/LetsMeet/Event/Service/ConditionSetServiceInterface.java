package com.LetsMeet.LetsMeet.Event.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Event.Model.Constraint;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Variable;

public interface ConditionSetServiceInterface {

    // Instantiating new ConditionSet
    public abstract ConditionSet createEmpty(); // Create new with no parameters
    public abstract ConditionSet createDefault(); // Create with default parameters

    public abstract boolean delete(UUID conditionSet);

    // Generic management
    public abstract Boolean addVariable(UUID conditionSet, Variable<?> variable);
    public abstract Boolean addConstraint(UUID conditionSet, Constraint<?> constraint);

    /* Specific helper functions */
    // TimeRange 
    public abstract void addTimeRanges(UUID uuid, List<DateTimeRange> ranges); 
    public abstract Optional<List<DateTimeRange>> getTimeRange(UUID uuid);

    // Services
    public abstract void setServices(UUID eventUuid, List<String> services);
    public abstract List<String> getServices(UUID event);


}
