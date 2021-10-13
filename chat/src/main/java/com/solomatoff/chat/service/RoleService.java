package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.store.IRoleStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {
    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Transactional
    @Override
    public Optional<Role> saveOrUpdate(Role role) {
        return roleStore.saveOrUpdate(role);
    }

    @Override
    public Collection<Role> findAll() {
        return roleStore.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleStore.findById(id);
    }

    @Override
    public void delete(Role role) {
        roleStore.delete(role);
    }
}
