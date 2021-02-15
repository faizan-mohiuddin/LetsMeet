package com.LetsMeet.LetsMeet.Business.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class BusinessDAO implements DAO<Business> {

    @Autowired
    DBConnector database;

    @Override
    public Optional<Business> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Business>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(Business business) {
        return null;
    }

    @Override
    public Boolean update(Business business) {
        return null;
    }

    @Override
    public Boolean delete(Business business) {
        return null;
    }

    @Override
    public Boolean delete(UUID uuid) {
        return null;
    }
}
