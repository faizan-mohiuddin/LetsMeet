//-----------------------------------------------------------------
// Constraint.java
// Let's Meet 2021
//
// Let's Meet specific model class for variable as part of condition set system

package com.LetsMeet.Models.Data;

//-----------------------------------------------------------------

import java.util.UUID;

//-----------------------------------------------------------------

public class Constraint<T> {

    UUID uuid;
    String name;

    //TODO replace with pair/tuple like type
    Variable<T> first;
    Variable<T> second;

    
    //TODO replace string with enum
    Character relation;
    Boolean resolveTrue;        // If false the relation between scope variables must be false for constraint to hold
    Double priority;


    public Constraint(String uuid, String name, Variable<T> scopeFirst, Variable<T> scopeSecond, Character relation){
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.first = scopeFirst;
        this.second = scopeSecond;
        this.relation = relation;
        this.resolveTrue = true;
        this.priority = 1.0;
    }

    
}
