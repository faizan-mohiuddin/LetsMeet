package com.LetsMeet.LetsMeet.Event.Service;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

@Service
public class ConditionSetService implements ConditionSetServiceInterface {

    private static final Logger LOGGER=LoggerFactory.getLogger(ConditionSetService.class);

    @Autowired
    ConditionSetDao dao;

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

    @Override
    public Boolean addVariable(UUID conditionSet, Variable<?> variable) {
        try{
            ConditionSet set = dao.get(conditionSet).get();
            set.addVariable(variable);
            dao.update(set);
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not add new variable", e);
            return false;
        }
    }

    @Override
    public Boolean addConstraint(UUID conditionSet, Constraint<?> constraint) {
        try{
            ConditionSet set = dao.get(conditionSet).get();
            set.addConstraint(constraint);;
            dao.update(set);
            return true;
        }
        catch( Exception e){
            LOGGER.error("Could not add new constraint", e);
            return false;
        }
    }

    @Override
    public void addTimeRanges(UUID uuid, List<DateTimeRange> ranges) {
        try{
            ConditionSet set = dao.get(uuid).get();
            for (DateTimeRange i : ranges){
                set.getVariable("event_time").append(i);
            }
            dao.update(set);
        }
        catch( Exception e){
            LOGGER.error("Could not add to event_time:", e);
        }
    }

    @Override
    public Optional<List<DateTimeRange>> getTimeRange(UUID uuid) {
        try{
            return Optional.ofNullable(dao.get(uuid).get().getVariable("event_time").getDomain());
        }
        catch( Exception e){
            LOGGER.error("Could not get event_time:", e);
            return Optional.empty();
        }
    }

    public Boolean clearTimeRange(UUID uuid){
        ConditionSet set = dao.get(uuid).orElseThrow(IllegalArgumentException::new);
        set.getVariable("event_time").clearDomain();
        dao.update(set);
        return true;
        //return (set.removeVariable("event_time")) ? dao.update(set) : false;
    }

    @Override
    public void setServices(UUID eventUuid, List<String> services) {
        // TODO Auto-generated method stub

    }

    @Override 
    public List<String> getServices(UUID event) {
        // TODO Auto-generated method stub
        return null;
    }


}
