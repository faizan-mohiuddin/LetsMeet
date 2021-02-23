package com.LetsMeet.LetsMeet.Business.Venue.DAO;

import com.LetsMeet.LetsMeet.Business.Venue.Model.VenueBusiness;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class VenueBusinessDAO implements DAOconjugate<VenueBusiness> {
    @Override
    public Optional<VenueBusiness> get(UUID uuid1, UUID uuid2) {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<VenueBusiness>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(VenueBusiness venueBusiness) {
        return null;
    }

    @Override
    public Boolean update(VenueBusiness venueBusiness) {
        return null;
    }

    @Override
    public Boolean delete(VenueBusiness venueBusiness) {
        return null;
    }

    @Override
    public Boolean delete(String uuid1, String uuid2) {
        return null;
    }
}
