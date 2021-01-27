// Very basic test

package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.ConditionSetData;
import com.LetsMeet.Models.Constraint;
import com.LetsMeet.Models.Variable;

public class ConditionSetData_test {

    public static String uuid = "cf6a5348-60af-11eb-ae93-0242ac130002";


    public static void main(String[] args) {
        Integer[] nums = {1,2,3,4};
        Variable<Integer> var1 = new Variable<Integer>(uuid, "var1", nums);
        Constraint con1 = new Constraint(uuid, "con1", var1, var1, '=');
        
        
        Variable<?>[] varArray = {var1};
        Constraint[] conArray = {con1};
        

		ConditionSetData set = new ConditionSetData(ConditionSetData_test.uuid, "ConSet1", varArray, conArray);
	}
}
