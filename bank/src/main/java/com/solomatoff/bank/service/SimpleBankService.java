package com.solomatoff.bank.service;

import com.solomatoff.bank.domain.User;
import com.solomatoff.bank.repository.UserRepository;
import com.solomatoff.bank.domain.Account;
import com.solomatoff.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimpleBankService implements BankService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;


    public SimpleBankService(UserRepository userRepository,
                             AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void addUser(User user) {
        this.userRepository.saveOrUpdate(user);
    }

    @Override
    public void addAccount(String passport, Account account) {
        this.userRepository.findByPassport(passport)
                .ifPresent(u -> {
                    account.setUser(u);
                    accountRepository.saveOrUpdate(account);
                    u.getAccounts().add(account);
                });
    }

    @Override
    public Optional<User> findByPassport(String passport) {
        return this.userRepository.findByPassport(passport);
    }

    @Override
    public Optional<Account> findByRequisite(String passport, String requisite) {
        return accountRepository.findByRequisite(passport, requisite);
    }

    @Override
    public boolean transferMoney(String srcPassport, String srcRequisite, String destPassport, String destRequisite, double amount) {
        var srcAccount = findByRequisite(srcPassport, srcRequisite);
        if (srcAccount.isEmpty()) {
            return false;
        }
        var descAccount = findByRequisite(destPassport, destRequisite);
        if (descAccount.isEmpty()) {
            return false;
        }
        if (srcAccount.get().getBalance() - amount < 0) {
            return false;
        }
        srcAccount.get().setBalance(srcAccount.get().getBalance() - amount);
        descAccount.get().setBalance(descAccount.get().getBalance() + amount);
        return false;
    }

}