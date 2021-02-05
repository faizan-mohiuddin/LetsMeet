package com.LetsMeet.LetsMeet;

import com.LetsMeet.Models.*;
import jdk.jfr.Event;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

public class UserManager {

    public UserManager(){

    }

    public static boolean checkIfOwner(String EventUUID, String UserUUID){
        // Returns true if the user is the owner of the event
        EventsModel model = new EventsModel();
        List<HasUsersRecord> records = model.getHasUsers(EventUUID, UserUUID);
        model.close();

        for(HasUsersRecord r : records){
            if(r.IsOwner){
                return true;
            }
        }
        return false;
    }

}
