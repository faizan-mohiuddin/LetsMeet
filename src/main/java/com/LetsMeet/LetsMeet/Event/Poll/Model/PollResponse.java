package com.LetsMeet.LetsMeet.Event.Poll.Model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetEntity;

public class PollResponse extends LetsMeetEntity implements Map<String,Boolean>{
    
    Map<String,Boolean> responses;

    public PollResponse(UUID uuid, Map<String, Boolean> response) {
        super(uuid);
        this.responses = response;
    }

    public PollResponse(Map<String, Boolean> response){
        this(UUID.randomUUID(), response);
    }

    public Map<String, Boolean> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, Boolean> response) {
        this.responses = response;
    }

    public boolean setOption(String option, boolean value){
        return this.responses.put(option, value);
    }

    @Override
    public int size() {
        return this.responses.size();
    }

    @Override
    public boolean isEmpty() {
        return this.responses.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.responses.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.responses.containsValue(value);
    }

    @Override
    public Boolean get(Object key) {
        return this.responses.get(key);
    }

    @Override
    public Boolean put(String key, Boolean value) {
        return this.responses.put(key, value);
    }

    @Override
    public Boolean remove(Object key) {
        return this.responses.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Boolean> m) {
        this.responses.putAll(m);
    }

    @Override
    public void clear() {
        this.clear();      
    }

    @Override
    public Set<String> keySet() {
        return this.responses.keySet();
    }

    @Override
    public Collection<Boolean> values() {
        return this.responses.values();
    }

    @Override
    public Set<Entry<String, Boolean>> entrySet() {
        return this.responses.entrySet();
    }


}
