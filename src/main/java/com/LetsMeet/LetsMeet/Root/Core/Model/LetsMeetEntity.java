package com.LetsMeet.LetsMeet.Root.Core.Model;

import java.util.UUID;

public class LetsMeetEntity {

    public UUID uuid;
    public String name;

    public LetsMeetEntity(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public LetsMeetEntity(String name) {
        this(UUID.randomUUID(), name);
    }

    public LetsMeetEntity(UUID uuid){
        this(uuid, "entity_" + uuid.toString());
    }

    public LetsMeetEntity() {
        this.uuid = UUID.randomUUID();
        this.name = "entity_" + this.uuid.toString();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
