package com.LetsMeet.LetsMeet.Venue.Model;

import java.util.UUID;

public class VenueBusiness {
    UUID businessUUID;
    UUID venueUUID;

    public VenueBusiness(UUID businessUUID, UUID venueUUID){
        this.businessUUID = businessUUID;
        this.venueUUID = venueUUID;
    }

    public UUID getVenueUUID() {
        return venueUUID;
    }

    public UUID getBusinessUUID() {
        return businessUUID;
    }
}
