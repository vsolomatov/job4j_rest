package com.solomatoff.chat.v1controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.*;
import com.solomatoff.chat.service.IMessageService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class V1MessageControllerTest {

    private static final String API_BASE = "/api/v1/person/";
    private static long personId;
    private static long roomId;
    private static long messageId;
    private static Message message;
    private static UserDetails userDetails;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private IPersonService personService;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserDetailsService userDetailsService;

    @BeforeEach
    public void init() throws Exception {
        Person person = new Person("solomatov.vyacheslav", "F#12W3");
        person.addRole(new Role(RoleType.ROLE_USER));
        Room room = new Room("Melanie Thandiwe", "Newton", person);
        new Message("Test message 1", person, room);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            roomId = value.getRooms().iterator().next().getId();
            message = value.getRooms().iterator().next().getMessages().iterator().next();
            messageId = message.getId();
            userDetails = userDetailsService.loadUserByUsername(value.getLogin());
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    public void testGetAllMessages() throws Exception {
        var person = personService.findById(personId);
        if (person.isPresent()) {
            Person per = person.get();
            Set<Room> rooms = per.getRooms();
            for (Room room : rooms) {
                Set<Message> messages = room.getMessages();
                this.mockMvc.perform(
                        MockMvcRequestBuilders.get(API_BASE + "/" + personId + "/room/" + room.getId() + "/message/")
                                .with(user(userDetails)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.content().json(MAPPER.writeValueAsString(messages), false)
                        );
            }
        }
    }


    @Test
    public void testMessageExists() throws Exception {
        var person = personService.findById(personId);
            this.mockMvc.perform(
                    get(API_BASE  + "/" + personId + "/room/" + roomId + "/message/" + messageId)
                            .with(user(userDetails)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(messageId)
                    );
    }

    @Test
    public void testMessageNotFound() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "/" + personId + "/room/" + roomId + "/message/" + "0")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().is4xxClientError()
                );
    }


    @Test
    public void testCreateMessage() throws Exception {
        var person = personService.findById(personId);
        var room = roomService.findById(roomId);

        if (person.isPresent() && room.isPresent()) {
            Message message = new Message("Test message 2", person.get(), room.get());

            mockMvc.perform(
                    post(API_BASE  + "/" + personId + "/room/" + roomId + "/message/")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(message)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.messageText").value("Test message 2")
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testUpdateMessage() throws Exception {
        // Поскольку поля author, room имеют атрибут "updatable = false", их изменять не будем, только текст
        message.setMessageText("Test message text 4");

        mockMvc.perform(
                put(API_BASE + "/" + personId + "/room/" + roomId + "/message/")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(message)))
                .andExpect(status().isOk()
                );

        Optional<Message> optionalMessage = messageService.findById(messageId);
        if (optionalMessage.isPresent()) {
            Assertions.assertEquals("Test message text 4", optionalMessage.get().getMessageText());
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testDeleteMessage() throws Exception {
        mockMvc.perform(
                delete(API_BASE  + "/" + personId + "/room/" + roomId + "/message/" + messageId)
                        .with(user(userDetails)))
                .andExpect(status().isOk()
                );
        var message = messageService.findById(messageId);
        Assertions.assertFalse(message.isPresent());
    }

}
