package com.solomatoff.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class RoomJsonTest {

    private static long personId;
    private static long roomId;

    @Autowired
    IPersonService personService;

    @Autowired
    IRoomService roomService;

    @BeforeEach
    public void init() {
        Person person = new Person("solomatov", "123");
        Room room = new Room("Scarlett", "Johansson", person);
        new Message("Test message 1", person, room);
        new Message("Test message 2", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            roomId = value.getRooms().iterator().next().getId();
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalRoom = personService.findById(personId);
        optionalRoom.ifPresent(personService::delete);
    }

    @Test
    void testJson() throws JsonProcessingException {
        Optional<Room> roomFromDb = roomService.findById(roomId);
        if (roomFromDb.isPresent()) {
            Room room = roomFromDb.get();
            String jsonResult = new ObjectMapper().writeValueAsString(room);
            Assertions.assertTrue(jsonResult.contains("Scarlett"));
            Assertions.assertTrue(jsonResult.contains("Test message 2"));
        }

    }
}