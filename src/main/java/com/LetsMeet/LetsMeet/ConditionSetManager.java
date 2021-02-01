//-----------------------------------------------------------------
// ConditionSetManager.java
// Let's Meet 2021
//
// Responsible for instantiating and performing high level operation on a LetsMeet.Models.ConditionSet object

package com.LetsMeet.LetsMeet;

//-----------------------------------------------------------------

import com.LetsMeet.Models.*;

//-----------------------------------------------------------------

public class ConditionSetManager {
    
    ConditionSet conditionSet;

    // Constructors
    //-----------------------------------------------------------------

    // Instantiate manager with a brand new ConditionSet
    public ConditionSetManager(){
        // Okay this is sketchy
        this.conditionSet = ConditionSetManager.generate().conditionSet;
    }

    // Instantiate manager with exising ConditionSet
    public ConditionSetManager(ConditionSet conditionSet){
        this.conditionSet = conditionSet;
    }

    // Instantiate manager with ConditoinSet constructed by model (i.e from DB)
    public ConditionSetManager(String uuid){
        EventsModel model = new EventsModel();
        this.conditionSet = model.getConditionSetByUUID(uuid);     //TODO handle failure
        model.closeCon();
    }


    //-----------------------------------------------------------------

    // Creates a new ConditionSet and returns a ConditoinSetManager for it
    public static ConditionSetManager generate(){

        //TODO Need a general utility class to generate secure, random UUIDs
        String ConditionSetUUID = "00000000-0000-0000-0000-000000000000";
        ConditionSetManager manager = new ConditionSetManager(new ConditionSet(ConditionSetUUID));

        manager.save();

        return manager;
    }


    // Update in model
    public void save(){
        EventsModel model = new EventsModel();
        System.out.println("TODO -- Save ConditionSet to model");
        //TODO model.SetConditionSet(args);
        model.closeCon();
    }

    // Add variables to ConditionSet
    public void addVariable(Variable<?> variable){
        conditionSet.addVariable(variable);
        this.save();
    }

    public void addVariable(Variable<?>[] variables){
        for (Variable<?> e : variables){
            this.addVariable(e);
        }
    }

    // Add constraints to ConditionSet
    public void addConstraint(Constraint<?> constraint){
        this.conditionSet.addConstraint(constraint);
        this.save();
    }

    public void addConstraint(Constraint<?>[] constraints){
        for (Constraint<?> e : constraints){
            this.addConstraint(e);
        }
    }



}
