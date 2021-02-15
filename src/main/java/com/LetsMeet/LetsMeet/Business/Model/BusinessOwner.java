package com.LetsMeet.LetsMeet.Business.Model;

import java.util.UUID;

public class BusinessOwner {
    UUID businessUUID;
    UUID userUUID;

    public BusinessOwner(UUID businessUUID, UUID userUUID){
        this.businessUUID = businessUUID;
        this.userUUID = userUUID;
    }
}
