package com.solomatoff.bank.repository;

import com.solomatoff.bank.domain.User;
import org.springframework.stereotype.Repository;
import com.solomatoff.bank.store.Store;

import java.util.Optional;

@Repository
public class UserRepository extends Store<User> {

    public Optional<User> findByPassport(String passport) {
        return store.values().stream()
                .filter(u -> u.getPassword().equals(passport))
                .findFirst();
    }

}
