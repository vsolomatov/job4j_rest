package com.solomatoff.chat.v1controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.solomatoff.chat.domain.*;
import com.solomatoff.chat.service.IPersonService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class V1PersonControllerTest {

    private static final String API_BASE = "/api/v1/person/";
    private static long personId;
    private static Person person;
    private static UserDetails userDetails;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private IPersonService personService;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserDetailsService userDetailsService;

    @BeforeEach
    public void init() {
        Set<RoleType> roleTypeSet = new HashSet<>();
        roleTypeSet.add(RoleType.ROLE_ADMIN);
        person = new Person("solomatov", encoder.encode("321"));
        person.addRoles(roleTypeSet);
        Room room1 = new Room("Morgan", "Freeman", person);
        new Message("Test message text in Room 1", person, room1);
        Room room2 = new Room("Scarlett", "Johansson", person);
        new Message("Test message text in Room 2", person, room2);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);

        optionalPerson.ifPresent(value -> {
            person = value;
            personId = value.getId();
            userDetails = userDetailsService.loadUserByUsername(value.getLogin());
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    public void testGetAllPersons() throws Exception {
        List<Person> personList = new ArrayList<>(personService.findAll());
        this.mockMvc.perform(
                        get(API_BASE)
                                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(MAPPER.writeValueAsString(personList), false)
                );
    }


    @Test
    public void testPersonExists() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + personId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personId)
                );
    }

    @Test
    public void testPersonNotFound() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "0")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().is4xxClientError()
                );
    }


    @Test
    public void testCreatePerson() throws Exception {
        /*
            Исходя из требований метода создания Person в контроллере V1PersonController
            мы можем создавать Person только если у него пустые множества ролей и комнат.
            Роли и комнаты создаются дополнительными запросами к другим REST API
        */
        Person person = new Person("solomatoff.70", "123");

        ResultActions resultActions = mockMvc.perform(
                        post(API_BASE)
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("solomatoff.70")
                );

        // Получим идентификатор Person из ответа
        MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // Удалим созданный объект
        var optionalPerson = personService.findById(id.longValue());
        optionalPerson.ifPresent(personService::delete);

        person = new Person("solomatoff.70", "123");
        person.addRole(new Role(RoleType.ROLE_USER));

        mockMvc.perform(post(API_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePerson() throws Exception {
        person.setPassword("321");
        mockMvc.perform(
                        put(API_BASE)
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(person)))
                .andExpect(status().isOk()
                );
        var optionalPerson = personService.findById(personId);
        if (optionalPerson.isPresent()) {
            Person person1 = optionalPerson.get();
            Assertions.assertTrue(encoder.matches("321", person1.getPassword()));
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testDeletePerson() throws Exception {
        mockMvc.perform(
                        delete(API_BASE + personId)
                                .with(user(userDetails)))
                .andExpect(status().isOk()
                );
        var person = personService.findById(personId);
        Assertions.assertFalse(person.isPresent());
    }

}
