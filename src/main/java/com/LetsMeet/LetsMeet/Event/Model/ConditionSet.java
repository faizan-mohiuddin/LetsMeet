//-----------------------------------------------------------------
// ConditionSet.java
// Let's Meet 2021
//
// Let's Meet specific model class for condition set system

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.UUID;
import java.io.Serializable;
import java.util.LinkedHashMap;

//-----------------------------------------------------------------

public class ConditionSet implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    UUID uuid;
    String name;
    LinkedHashMap<String, Variable<?>> variables;
    LinkedHashMap<String,Constraint<?>> constraints;

    // Primary constructor
    public ConditionSet(UUID uuid, String name,Variable<?>[] variables, Constraint<?>[] constraints){
        this.uuid = uuid;
        this.name = name;
        this.variables = new LinkedHashMap<>();
        this.constraints = new LinkedHashMap<>();

        // Add contents of variable array
        for (Variable<?> e : variables){
            this.variables.put(e.key,e);
        }

        // Add contents of constraint array
        for (Constraint<?> e : constraints){
            this.constraints.put(e.name,e);
        }
    }

    // Constructor with only UUID
    public ConditionSet(UUID uuid){
        this(uuid,uuid.toString(),new Variable<?>[0], new Constraint<?>[0]);
    }

    // Getters
    //-----------------------------------------------------------------

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public Variable getVariable(String key){
        return this.variables.get(key);
    }

    // Setters
    //-----------------------------------------------------------------

    public void setName(String name){
        this.name = name;
    }

    public void setVariables (LinkedHashMap<String, Variable<?>> variables){
        this.variables= variables;
    }

    public void setConstraints (LinkedHashMap<String,Constraint<?>> constraints){
        this.constraints = constraints;
    }


    // Add
    //-----------------------------------------------------------------
    

    public void addVariable(Variable<?> variable){
            this.variables.put(variable.key, variable);
    }   

    public void addConstraint(Constraint<?> constraint){
        this.constraints.put(constraint.uuid.toString(), constraint);
    }

    // Delete
    //-----------------------------------------------------------------

    public Boolean removeVariable(String key){
        return (this.variables.remove(key) != null) ? true : false;
    }

    public Boolean removeConstraint(String key){
        return (this.constraints.remove(key) != null) ? true : false;
    }


}
