package com.LetsMeet.LetsMeet.Utilities;

import java.util.Collection;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(String uuid);
    Collection<T> getAll();
    int save(T t);
    void update(T t);
    void delete(T t);
    void delete(String uuid);
}
