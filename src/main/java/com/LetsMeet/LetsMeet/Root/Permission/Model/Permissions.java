package com.LetsMeet.LetsMeet.Root.Permission.Model;

import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetEntity;

public class Permissions {

    private Permissions() {
        throw new IllegalStateException("Utility class");
      }
    
    public static Permission create(LetsMeetEntity parent, LetsMeetEntity child, boolean edit){
        return new Permission(parent.getUUID(), child.getUUID(), edit ? Permission.Type.READ : Permission.Type.WRITE);
    }

    public static Permission create(LetsMeetEntity parent, LetsMeetEntity child, String code){
        return new Permission(parent.getUUID(), child.getUUID(), Permission.Type.valueOfCode(code));
    }

    public static Permission create(UUID parent, UUID child, String code){
        return new Permission(parent, child, Permission.Type.valueOfCode(code));
    }
}
