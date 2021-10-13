package com.solomatoff.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.service.IPersonService;
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
class PersonJsonTest {

    private static long personId;

    @Autowired
    IPersonService personService;

    @BeforeEach
    public void init() {
        Set<RoleType> roleTypeSet = new HashSet<>();
        roleTypeSet.add(RoleType.ROLE_USER);
        roleTypeSet.add(RoleType.ROLE_ADMIN);
        roleTypeSet.add(RoleType.ROLE_SUPERVISOR);
        Person person = new Person("solomatov", "123");
        person.addRoles(roleTypeSet);

        // Создаем первую Room
        Room room1 = new Room("Morgan", "Freeman", person);
        new Message("Test message in Room 1", person, room1);

        // Создаем вторую Room
        Room room2 = new Room("Scarlett", "Johansson", person);
        new Message("Test message in Room 2", person, room2);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    void testJson() throws JsonProcessingException {
        Optional<Person> personFromDb = personService.findById(personId);
        if (personFromDb.isPresent()) {
            Person person = personFromDb.get();
            String jsonResult = new ObjectMapper().writeValueAsString(person);
            Assertions.assertTrue(jsonResult.contains("Test message in Room 1"));
            Assertions.assertTrue(jsonResult.contains("Test message in Room 2"));
            Assertions.assertTrue(jsonResult.contains("Morgan"));
            Assertions.assertTrue(jsonResult.contains("Scarlett"));
            Assertions.assertTrue(jsonResult.contains(RoleType.ROLE_USER.toString()));
        }
    }

}