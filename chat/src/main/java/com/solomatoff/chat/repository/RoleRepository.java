package com.solomatoff.chat.repository;

import com.solomatoff.chat.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
}
