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
class RoomSaveOrUpdateTest {

    private static Long personId;
    private static Person person;
    private static Room room;

    @Autowired
    IPersonService personService;

    @Autowired
    IRoomService roomService;


    @BeforeEach
    public void init() {
        person = new Person("solomatov", "123");
        person.addRole(new Role(RoleType.ROLE_ADMIN));
        room = new Room("Morgan", "Freeman", person);
        new Message("Test message 1", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            person = value;
            personId = value.getId();
            room = (Room) value.getRooms().toArray()[0];
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    void testUpdateRoom() throws Exception {
        room.setName("Test room 2");
        Optional<Room> optionalRoom = roomService.saveOrUpdate(room);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            Assertions.assertEquals("Test room 2", room.getName());
        }
    }

    @Test
    void testCreateRoom() throws Exception {
        if (person != null && room != null) {
            Room room = new Room("Test room 3", "Description test room 3", person);
            Optional<Room> optionalRoom = roomService.saveOrUpdate(room);
            if (optionalRoom.isPresent()) {
                room = optionalRoom.get();
                Assertions.assertEquals("Test room 3", room.getName());
                Assertions.assertEquals("Description test room 3", room.getDescription());
            }
        } else {
            throw new RuntimeException("Test failed");
        }
    }

}