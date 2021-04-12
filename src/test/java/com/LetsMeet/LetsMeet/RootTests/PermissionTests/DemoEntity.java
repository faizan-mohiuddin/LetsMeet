package com.LetsMeet.LetsMeet.RootTests.PermissionTests;

import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetEntity;

public class DemoEntity extends LetsMeetEntity {

    UUID uuid;

    public DemoEntity(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
    
}
