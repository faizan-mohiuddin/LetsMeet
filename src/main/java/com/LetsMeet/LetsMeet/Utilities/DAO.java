package com.LetsMeet.LetsMeet.Utilities;

import java.util.Collection;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(String uuid);
    int save(T t);
    void update(T t);
    String delete(T t);
}
