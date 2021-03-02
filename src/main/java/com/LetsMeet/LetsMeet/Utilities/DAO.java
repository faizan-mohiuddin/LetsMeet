package com.LetsMeet.LetsMeet.Utilities;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DAO<T> {
    Optional<T> get(UUID uuid);
    Optional<Collection<T>> getAll();
    Boolean save(T t) throws IOException;
    Boolean update(T t);
    Boolean delete(T t);
    Boolean delete(UUID uuid);
}
