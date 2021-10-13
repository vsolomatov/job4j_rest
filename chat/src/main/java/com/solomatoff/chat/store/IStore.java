package com.solomatoff.chat.store;


import java.util.Collection;
import java.util.Optional;

public interface IStore<T> {
    Collection<T> findAll();

    Optional<T> findById(long id);

    Optional<T> saveOrUpdate(T employee);

    void delete(T employee);

}
