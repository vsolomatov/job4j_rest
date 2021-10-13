package com.solomatoff.bank.repository;

import com.solomatoff.bank.domain.Account;
import org.springframework.stereotype.Repository;
import com.solomatoff.bank.store.Store;

import java.util.Optional;

@Repository
public class AccountRepository extends Store<Account> {

    public Optional<Account> findByRequisite(String passport, String requisite) {
        return store.values().stream()
                .filter(a -> a.getUser().getPassword().equals(passport)
                        && a.getRequisite().equals(requisite))
                .findFirst();
    }

}
