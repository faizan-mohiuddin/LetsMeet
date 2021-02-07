package com.LetsMeet.LetsMeet.Utilities;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DAOconjugate<T> {
    Optional<T> get(UUID uuid1, UUID uuid2);
    Optional<Collection<T>> getAll();
    Boolean save(T t);
    Boolean update(T t);
    Boolean delete(T t);
    Boolean delete(UUID uuid1, UUID uuid2);
}
