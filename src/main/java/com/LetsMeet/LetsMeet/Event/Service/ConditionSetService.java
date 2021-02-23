//-----------------------------------------------------------------
// ConditionSetService.java
// Let's Meet 2021
//
// Operations possible on a condition set
package com.LetsMeet.LetsMeet.Event.Service;

//-----------------------------------------------------------------

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.LetsMeet.LetsMeet.Event.DAO.ConditionSetDao;
import com.LetsMeet.LetsMeet.Event.Model.ConditionSet;
import com.LetsMeet.LetsMeet.Event.Model.Constraint;
import com.LetsMeet.LetsMeet.Event.Model.DateTimeRange;
import com.LetsMeet.LetsMeet.Event.Model.Variable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//-----------------------------------------------------------------

@Service
public class ConditionSetService implements ConditionSetServiceInterface {

    // Constants definitions
    //-----------------------------------------------------------------
    private static String EVENT_TIME = "event_time";
    private static String EVENT_SERVICE = "event_services";

    // Components
    //-----------------------------------------------------------------

    private static final Logger LOGGER=LoggerFactory.getLogger(ConditionSetService.class);

    @Autowired
    ConditionSetDao dao;


    // ConditionSet helpers for creation
    //-----------------------------------------------------------------

    @Override
    public ConditionSet createEmpty() {
        return new ConditionSet(UUID.randomUUID());
    }

    @Override
    public ConditionSet createDefault() {
        ConditionSet set = this.createEmpty();
        set.addVariable(new Variable<DateTimeRange>(UUID.randomUUID(), "event_time", new DateTimeRange[0]));
        set.addVariable(new Variable<String>(UUID.randomUUID(), "event_service", new String[0]));

        dao.save(set);

        return set;
    }

    @Override
    public boolean delete(UUID conditionSet){
        dao.delete(conditionSet);
        return true;
    }

    public ConditionSet get(UUID conditionSetUUID){
        return dao.get(conditionSetUUID).get();
    }

    //-----------------------------------------------------------------
    // Contents management - Variables
    //-----------------------------------------------------------------

    @Override
    public Boolean addVariable(ConditionSet conditionSet, Variable<? extends Serializable> variable) {
        try{
            // Append the data to existing variable if 
            if (conditionSet.getVariable(variable.getKey()) != null){
                return appendVariable(conditionSet.getVariable(variable.getKey()), variable);
            }
            else{
                conditionSet.addVariable(variable);
                dao.update(conditionSet);
            }
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not add new variable", e);
            return false;
        }
    }

    // Appends the contents of one variable to the existing one.
    private Boolean appendVariable(Variable<Serializable> variable1, Variable<? extends Serializable> variable){

        for (Serializable o : variable.getDomain()){
            variable1.append(o);
        }
        return true;
    }

    public Boolean removeVariable(ConditionSet conditionSet, String key){
        try{
            conditionSet.removeVariable(key);
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not remove variable", e);
            return false;
        }  
    }

    // Helpers for common variable types
    //-----------------------------------------------------------------

    // EVENT_TIME

    @Override
    public void addTimeRanges(ConditionSet conditionSet, List<DateTimeRange> ranges) {
        try{
            addVariable(conditionSet, new Variable<>(EVENT_TIME, ranges));
            dao.update(conditionSet);
        }
        catch( Exception e){
            LOGGER.error("Could not add to event_time: {}", e.getMessage());
        }
    }

    @Override
    public Optional<List<DateTimeRange>> getTimeRange(ConditionSet conditionSet) {
        try{
            return Optional.ofNullable(conditionSet.getVariable(EVENT_TIME).getDomain());
        }
        catch( Exception e){
            LOGGER.error("Could not get event_time: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Boolean clearTimeRange(ConditionSet conditionSet){
        try{
            removeVariable(conditionSet, EVENT_TIME);
            dao.update(conditionSet);
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not delete event_time: {}", e.getMessage());
            return false;
        }
    }

    // EVENT_SERVICES
    //TODO
    
    @Override
    public void setServices(ConditionSet conditionSet, List<String> services) {
        // TODO Auto-generated method stub

    }

    @Override 
    public List<String> getServices(UUID event) {
        // TODO Auto-generated method stub
        return null;
    }

    // Contents management - Constraints
    //-----------------------------------------------------------------

    @Override
    public Boolean addConstraint(ConditionSet conditionSet, Constraint<?> constraint) {
        try{
            conditionSet.addConstraint(constraint);
            dao.update(conditionSet);
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not add new constraint", e);
            return false;
        }
    }
}
