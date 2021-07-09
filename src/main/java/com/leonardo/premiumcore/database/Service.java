package com.leonardo.premiumcore.database;

public interface Service<O, I> {

    void create(O object);

    O read(I id);

    void update(O object);

    void delete(O object);

    void deleteById(I id);

    Boolean exists(I id);

}
