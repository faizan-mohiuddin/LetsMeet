//-----------------------------------------------------------------
// ConditionSetData.java
// Let's Meet 2021
//
// Let's Meet specific model class for condition set system

package com.LetsMeet.Models;

//-----------------------------------------------------------------

import java.util.UUID;
import java.util.LinkedHashMap;

//-----------------------------------------------------------------

public class ConditionSetData {

    UUID uuid;
    String name;
    LinkedHashMap<UUID,Variable<?>> variables;
    LinkedHashMap<UUID,Object> constraints;

    
    public ConditionSetData(String uuid, String name,Variable<?>[] variables, Constraint[] constraints){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.variables = new LinkedHashMap<UUID,Variable<?>>();
        this.constraints = new LinkedHashMap<UUID,Object>();

        // Add contents of variable array
        for (Variable<?> e : variables){
            this.variables.put(e.uuid,e);
        }

        // Add contents of constraint array
        for (Constraint e : constraints){
            this.constraints.put(e.uuid,e);
        }
    }

    // Getters
    //-----------------------------------------------------------------

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public Object getVariable(UUID key){
        return this.variables.get(key);
    }

    // Setters
    //-----------------------------------------------------------------

    //TODO overwrite prevention
    public void addVariable(Variable<?> variable){
            this.variables.put(variable.uuid, variable);
    }   

    //TODO overwrite prevention
    public void addConstraint(Constraint constraint){
        this.constraints.put(constraint.uuid, constraint);
    }
}
