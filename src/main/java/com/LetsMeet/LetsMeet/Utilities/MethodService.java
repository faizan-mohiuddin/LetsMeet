package com.LetsMeet.LetsMeet.Utilities;

import java.util.ArrayList;
import java.util.List;

// Space for generic methods
public class MethodService {
    public static List<String> deepCopyStringList(List<String> l){
        List<String> newList = new ArrayList<>();
        for(int i = 0; i < l.size(); i++){
            newList.add(l.get(i));
        }
        return newList;
    }
}
