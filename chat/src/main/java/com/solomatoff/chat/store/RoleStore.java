package com.solomatoff.chat.store;

import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class RoleStore implements IRoleStore {

    private final RoleRepository roleRepository;

    public RoleStore(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Collection<Role> findAll() {
        return (Collection<Role>) roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> saveOrUpdate(Role role) {
        return Optional.of(roleRepository.save(role));
    }

    @Override
    public void delete(Role role) {
        roleRepository.delete(role);
    }

}