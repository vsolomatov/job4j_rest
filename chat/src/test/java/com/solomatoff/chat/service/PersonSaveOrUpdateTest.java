package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.*;
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
class PersonSaveOrUpdateTest {

    private static long personId;
    private static Person person;

    @Autowired
    IPersonService personService;

    @BeforeEach
    public void init() {
        person = new Person("solomatov", "123");
        person.addRole(new Role(RoleType.ROLE_ADMIN));
        Room room1 = new Room("Morgan", "Freeman", person);
        new Message("Test message text in Room 1", person, room1);
        Room room2 = new Room("Scarlett", "Johansson", person);
        new Message("Test message text in Room 2", person, room2);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);

        optionalPerson.ifPresent(value -> {
            person = value;
            personId = value.getId();
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    void testUpdatePerson() throws Exception {
        person.setLogin("solomatoff.vyacheslav");
        person.setPassword("321");
        Optional<Person> per = personService.saveOrUpdate(person);
        if (per.isPresent()) {
            Person person = per.get();
            Assertions.assertEquals("solomatoff.vyacheslav", person.getLogin());
            Assertions.assertEquals("321", person.getPassword());
        }
    }

}