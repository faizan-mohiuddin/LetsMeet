package com.LetsMeet.LetsMeet.Root.Media.VenueImages;

import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class VenueImage {
    public MultipartFile image;
    public String venueUUID;
    public java.util.UUID UUID;

    public VenueImage(MultipartFile i, String venueUUID){
        this.image = i;
        this.venueUUID = venueUUID;
        this.generateUUID();
    }

    public String getSaveName(){
        String response = String.format("%s_%s", this.UUID.toString(), this.image.getOriginalFilename());
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void generateUUID(){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = this.venueUUID + this.image.getOriginalFilename() + this.image.getSize() + "VenueImage" + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        this.UUID = uuid;
    }
}
