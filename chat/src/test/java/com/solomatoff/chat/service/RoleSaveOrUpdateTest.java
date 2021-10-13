package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.domain.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class RoleSaveOrUpdateTest {

    private static Long personId;
    private static Person person;
    private static Role role;

    @Autowired
    IPersonService personService;

    @Autowired
    IRoleService roleService;


    @BeforeEach
    public void init() {
        person = new Person("solomatov", "123");
        role = new Role(RoleType.ROLE_USER);
        person.addRole(role);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            person = value;
            personId = value.getId();
            role = (Role) value.getRoles().toArray()[0];
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    void testUpdateRole() throws Exception {
        role.setRoleType(RoleType.ROLE_ADMIN);
        Optional<Role> optionalRole = roleService.saveOrUpdate(role);
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            Assertions.assertEquals(role.getRoleType(), RoleType.ROLE_ADMIN);
        }
    }

    @Test
    void testCreateRole() throws Exception {
        if (person != null && role != null) {
            Role role = new Role(RoleType.ROLE_ADMIN);
            person.addRole(role);
            Optional<Role> optionalRole = roleService.saveOrUpdate(role);
            if (optionalRole.isPresent()) {
                role = optionalRole.get();
                Assertions.assertEquals(RoleType.ROLE_ADMIN, role.getRoleType());
            }
        } else {
            throw new RuntimeException("Test failed");
        }
    }

}