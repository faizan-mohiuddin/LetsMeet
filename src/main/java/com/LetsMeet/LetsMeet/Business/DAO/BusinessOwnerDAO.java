package com.LetsMeet.LetsMeet.Business.DAO;

import com.LetsMeet.LetsMeet.Business.Model.BusinessOwner;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class BusinessOwnerDAO implements DAOconjugate<BusinessOwner> {
    @Autowired
    DBConnector database;

    @Override
    public Optional<BusinessOwner> get(UUID uuid1, UUID uuid2) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<BusinessOwner>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(BusinessOwner businessOwner) {
        return null;
    }

    @Override
    public Boolean update(BusinessOwner businessOwner) {
        return null;
    }

    @Override
    public Boolean delete(BusinessOwner businessOwner) {
        return null;
    }

    @Override
    public Boolean delete(String uuid1, String uuid2) {
        return null;
    }
}
