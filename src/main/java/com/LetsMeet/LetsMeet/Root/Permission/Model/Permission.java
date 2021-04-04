package com.LetsMeet.LetsMeet.Root.Permission.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Permission {
    
    public enum Type{
        READ("0300"),
        WRITE("0200"),
        READ_WRITE("0600");

        public final String code;
        private Type(String code){this.code = code;}

        private static final Map<String, Type> TYPE_CODE = new HashMap<>();

        static {
            for (Type e: values()) {
                TYPE_CODE.put(e.code, e);
            }
        }
        public static Type valueOfCode(String code) {
            return TYPE_CODE.get(code);
        }
    }


    UUID parent;
    UUID child;
    Type type;


    public Permission(UUID parent, UUID child, Type type) {
        this.parent = parent;
        this.child = child;
        this.type = type;
    }
    public UUID getParent() {
        return parent;
    }
    public void setParent(UUID parent) {
        this.parent = parent;
    }
    public UUID getChild() {
        return child;
    }
    public void setChild(UUID child) {
        this.child = child;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }


}
