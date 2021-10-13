package com.solomatoff.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.*;
import com.solomatoff.chat.domain.Message;
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
class MessageSaveOrUpdateTest {

    private static Long personId;
    private static Person person;
    private static Room room;
    private static Message message;

    @Autowired
    IPersonService personService;

    @Autowired
    IMessageService messageService;


    @BeforeEach
    public void init() {
        person = new Person("solomatov", "123");
        room = new Room("Morgan", "Freeman", person);
        new Message("Test message 1", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            person = value;
            personId = value.getId();
            room = (Room) value.getRooms().toArray()[0];
            message = (Message) ((Room) value.getRooms().toArray()[0]).getMessages().toArray()[0];
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    void testUpdateMessage() throws Exception {
        message.setMessageText("Test message 2");
        Optional<Message> optionalMessage = messageService.saveOrUpdate(message);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            Assertions.assertEquals("Test message 2", message.getMessageText());
        }
    }

    @Test
    void testCreateMessage() throws Exception {
        if (person != null && room != null) {
            Message message = new Message("Test message 3", person, room);
            Optional<Message> optionalMessage = messageService.saveOrUpdate(message);
            if (optionalMessage.isPresent()) {
                message = optionalMessage.get();
                Assertions.assertEquals("Test message 3", message.getMessageText());
            }
        } else {
            throw new RuntimeException("Test failed");
        }
    }

}