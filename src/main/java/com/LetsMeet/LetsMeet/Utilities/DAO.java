package com.LetsMeet.LetsMeet.Utilities;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DAO<T> {
    Optional<T> get(UUID uuid);
    Collection<T> getAll();
    int save(T t);
    void update(T t);
    void delete(T t);
    void delete(UUID uuid);
}
