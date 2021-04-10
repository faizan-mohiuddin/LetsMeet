package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetEntity;

public class Poll extends LetsMeetEntity /*implements Map<String,Integer>*/  {
  
    private Map<String, Integer> options;
    private Boolean multiselect;


    public Poll(UUID uuid, String name, Map<String, Integer> options, Boolean selectMultiple) {
        super(uuid, name);
        this.options = options;
        this.multiselect = selectMultiple;
    }

    public Poll(String name, Boolean mutliselect, Map<String, Integer> options) {
        super(name);
        this.name = name;
        this.options = options;
        this.multiselect = mutliselect;
    }


    public Map<String, Integer> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Integer> options) {
        this.options = options;
    }

    public Boolean getSelectMultiple() {
        return multiselect;
    }

    public void setSelectMultiple(Boolean selectMultiple) {
        this.multiselect = selectMultiple;
    }

    //@Override
    public int size() {
        return this.options.size();
    }

    //@Override
    public boolean isEmpty() {
        return this.options.isEmpty();
    }

    //@Override
    public boolean containsKey(Object key) {
        return this.options.containsKey(key);
    }

    //@Override
    public boolean containsValue(Object value) {
        return this.options.containsValue(value);
    }

    //@Override
    public Integer get(Object key) {
        return this.options.get(key);
    }

    //@Override
    public Integer put(String key, Integer value) {
        return this.options.put(key, value);
    }

    //@Override
    public Integer remove(Object key) {
        return this.options.remove(key);
    }

    //@Override
    public void putAll(Map<? extends String, ? extends Integer> m) {
        this.options.putAll(m);
    }

    //@Override
    public void clear() {
        this.clear();      
    }

    //@Override
    public Set<String> keySet() {
        return this.options.keySet();
    }

    //@Override
    public Collection<Integer> values() {
        return this.options.values();
    }

    //@Override
    //public Set<Entry<String, Integer>> entrySet() {
    //    return this.options.entrySet();
    //}

    
}
