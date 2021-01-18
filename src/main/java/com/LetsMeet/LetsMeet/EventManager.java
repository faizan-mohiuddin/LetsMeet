package com.LetsMeet.LetsMeet;

import java.time.Instant;
import java.util.UUID;

public class EventManager {

    public static UUID createEventUUID(String name, String desc, String location){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + desc + location + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

}
