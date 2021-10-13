package com.solomatoff.bank.service;

import com.solomatoff.bank.domain.User;

import java.util.Collection;
import java.util.Optional;

public interface IUserService {

        Optional<User> saveOrUpdate(User model);
        Collection<User> findAll();
        User findById(int id);
        void delete(User model);
}
