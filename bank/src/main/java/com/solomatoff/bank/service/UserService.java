package com.solomatoff.bank.service;

import com.solomatoff.bank.domain.User;
import com.solomatoff.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> saveOrUpdate(User user) {
        return Optional.of(userRepository.saveOrUpdate(user));
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        var user = userRepository.findById(id);
        return user.orElse(null);

    }

    @Override
    public void delete(User user) {
        userRepository.delete(user.getId());
    }
}
