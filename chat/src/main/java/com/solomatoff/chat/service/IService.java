package com.solomatoff.chat.service;

import java.util.Collection;
import java.util.Optional;

public interface IService<T, ID> {

    Optional<T> saveOrUpdate(T model);
    Collection<T> findAll();
    Optional<T> findById(ID id);
    void delete(T model);

}
