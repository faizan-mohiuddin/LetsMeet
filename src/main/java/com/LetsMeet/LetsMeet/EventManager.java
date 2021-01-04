package com.LetsMeet.LetsMeet;

import java.util.UUID;

public class EventManager {

    public static UUID createEventUUID(String name, String desc, String location){
        String uuidData = name + desc + location;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

}
