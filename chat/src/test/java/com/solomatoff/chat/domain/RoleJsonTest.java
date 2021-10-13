package com.solomatoff.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
class RoleJsonTest {

    private static long personId;
    private static long roleId;

    @Autowired
    IPersonService personService;

    @Autowired
    IRoleService roleService;

    @BeforeEach
    public void init() {
        Set<RoleType> roleTypes = new HashSet<>();
        roleTypes.add(RoleType.ROLE_ADMIN);
        Person person = new Person("solomatov", "123");
        person.addRoles(roleTypes);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            roleId = value.getRoles().iterator().next().getId();
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalRole = personService.findById(personId);
        optionalRole.ifPresent(personService::delete);
    }

    @Test
    void testJson() throws JsonProcessingException {
        Optional<Role> roleFromDb = roleService.findById(roleId);
        if (roleFromDb.isPresent()) {
            Role role = roleFromDb.get();
            String jsonResult = new ObjectMapper().writeValueAsString(role);
            Assertions.assertTrue(jsonResult.contains(RoleType.ROLE_ADMIN.toString()));
        }

    }
}