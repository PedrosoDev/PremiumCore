package com.leonardo.premiumcore.database;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<O, T> {

    void commit(O obj);

    Optional<O> read(T id);

    void update(O obj);

    void delete(O obj);

    void deleteById(T id);

    List<O> findAll();

    boolean exists(T id);

    Class<O> getTarget();

}
