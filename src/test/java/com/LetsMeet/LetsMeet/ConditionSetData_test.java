// Very basic test - just a stopgap until junit tests are implimented

package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.ConditionSet;
import com.LetsMeet.Models.Constraint;
import com.LetsMeet.Models.Variable;

public class ConditionSetData_test {

    public static String uuid = "cf6a5348-60af-11eb-ae93-0242ac130002";


    public static void main(String[] args) {

        ConditionSetManager manager = new ConditionSetManager();


        Integer[] nums = {1,2,3,4};
        Variable<Integer> var1 = new Variable<Integer>(uuid, "var1", nums);
        Variable<Integer> var2 = new Variable<Integer>(uuid, "var2", nums);
        Constraint<Integer> con1 = new Constraint<Integer>(uuid, "con1", var1, var2, '=');
        
        manager.addVariable(var1);
        
        Variable<?>[] varArray = {var1,var2};
        Constraint<?>[] conArray = {con1};

        manager.addVariable(varArray);
        manager.addConstraint(conArray);

        manager.save();
        

		ConditionSet set = new ConditionSet(ConditionSetData_test.uuid, "ConSet1", varArray, conArray);
	}
}
