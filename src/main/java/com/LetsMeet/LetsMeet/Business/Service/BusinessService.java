package com.LetsMeet.LetsMeet.Business.Service;

import com.LetsMeet.LetsMeet.Business.DAO.BusinessDAO;
import com.LetsMeet.LetsMeet.Business.DAO.BusinessOwnerDAO;
import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BusinessService {
    @Autowired
    BusinessDAO DAO;

    @Autowired
    BusinessOwnerDAO ownerDAO;

    public String createBusiness(String name, User user){
        UUID uuid = this.generateUUID(name, user);
        Business business = new Business(uuid, name);

        // Add business to DB
        DAO.save(business);

        // Add connection between business and user
        BusinessOwner owner = new BusinessOwner(uuid, user.getUUID());
        ownerDAO.save(owner);

        return null;
    }

    // Private methods
    private UUID generateUUID(String name, User user){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String uuidData = name + user.getStringUUID() + user.getEmail() + user.getfName() + strTime;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

}
