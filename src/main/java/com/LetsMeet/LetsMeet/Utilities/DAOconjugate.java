package com.LetsMeet.LetsMeet.Utilities;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface DAOconjugate<T> {
    Optional<T> get(UUID uuid1, UUID uuid2) throws IOException;
    Optional<Collection<T>> getAll() throws IOException;
    Boolean save(T t) throws IOException;
    Boolean update(T t) throws IOException;
    Boolean delete(T t) throws IOException;
    Boolean delete(String uuid1, String uuid2) throws IOException;
}
