package com.solomatoff.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.service.IMessageService;
import com.solomatoff.chat.service.IPersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class MessageJsonTest {

    private static long personId;
    private static long messageId;

    @Autowired
    IPersonService personService;

    @Autowired
    IMessageService messageService;

    @BeforeEach
    public void init() {
        Person person = new Person("solomatov.70", "123");
        Room room = new Room("Scarlett", "Johansson", person);
        new Message("Test message 1", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            messageId = value.getRooms().iterator().next().getMessages().iterator().next().getId();
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalRoom = personService.findById(personId);
        optionalRoom.ifPresent(personService::delete);
    }

    @Test
    void testJson() throws JsonProcessingException {
        Optional<Message> messageFromDb = messageService.findById(messageId);
        if (messageFromDb.isPresent()) {
            Message message = messageFromDb.get();
            String jsonResult = new ObjectMapper().writeValueAsString(message);
            Assertions.assertTrue(jsonResult.contains("Test message 1"));
            Assertions.assertTrue(jsonResult.contains("solomatov.70"));
        }

    }
}