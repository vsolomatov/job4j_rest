package com.solomatoff.chat.v1controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.*;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class V1RoomControllerTest {

    private static final String API_BASE = "/api/v1/person/";

    private static long personId;
    private static long roomId;
    private static Room room;
    private static UserDetails userDetails;

    @Autowired
    private IPersonService personService;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserDetailsService userDetailsService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void init() {
        Person person = new Person("david.kon.70",  "123");
        room = new Room("Evan Rachel", "Wood", person);
        new Message("Evan Rachel 1", person, room);
        new Message("Evan Rachel 2", person, room);
        new Message("Evan Rachel 3", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            room = value.getRooms().iterator().next();
            roomId = room.getId();
            userDetails = userDetailsService.loadUserByUsername(value.getLogin());
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    public void testGetAllRooms() throws Exception {
        var person = personService.findById(personId);
        if (person.isPresent()) {
            Set<Room> rooms = person.get().getRooms();
            this.mockMvc.perform(
                    get(API_BASE + "/" + personId + "/room/")
                            .with(user(userDetails)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(MAPPER.writeValueAsString(rooms), false)
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }


    @Test
    public void testRoomExists() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "/" + personId + "/room/" + roomId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId)
                );
    }

    @Test
    public void testRoomNotFound() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "/" + personId + "/room/" + "0")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().is4xxClientError()
                );
    }


    @Test
    public void testCreateRoom() throws Exception {
        var person = personService.findById(personId);

        if (person.isPresent()) {
            Room room = new Room("Scarlett", "Johansson", person.get());
            mockMvc.perform(
                    post(API_BASE + "/" + personId + "/room/")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(room)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Scarlett")
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testUpdateRoom() throws Exception {
        room.setName("Scarlett");
        room.setDescription("Johansson");

        mockMvc.perform(
                put(API_BASE + "/" + personId + "/room/")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(room)))
                .andExpect(status().isOk()
                );
        Optional<Room> optionalRoom = roomService.findById(roomId);
        if (optionalRoom.isPresent()) {
            Assertions.assertEquals("Scarlett", optionalRoom.get().getName());
            Assertions.assertEquals("Johansson", optionalRoom.get().getDescription());
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testDeleteRoom() throws Exception {
        mockMvc.perform(
                delete(API_BASE + "/" + personId + "/room/" + roomId)
                        .with(user(userDetails)))
                .andExpect(status().isOk()
                );
        var room = roomService.findById(roomId);
        Assertions.assertFalse(room.isPresent());
    }

}
