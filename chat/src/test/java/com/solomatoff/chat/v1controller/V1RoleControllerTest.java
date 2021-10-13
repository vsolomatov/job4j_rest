package com.solomatoff.chat.v1controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.domain.RoleType;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoleService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class V1RoleControllerTest {

    private static final String API_BASE = "/api/v1/person/";

    private static long personId;
    private static long roleId;
    private static Role role;
    private static UserDetails userDetails;

    @Autowired
    private IPersonService personService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserDetailsService userDetailsService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void init() {
        Person person = new Person("david.kon.70",  "123");
        role = new Role(RoleType.ROLE_ADMIN);
        person.addRole(role);

        Optional<Person> optionalPerson = personService.saveOrUpdate(person);
        optionalPerson.ifPresent(value -> {
            personId = value.getId();
            role = value.getRoles().iterator().next();
            roleId = role.getId();
            userDetails = userDetailsService.loadUserByUsername(value.getLogin());
        });
    }

    @AfterEach
    public void destroy() {
        Optional<Person> optionalPerson = personService.findById(personId);
        optionalPerson.ifPresent(personService::delete);
    }

    @Test
    public void testGetAllRolesSuccessfully() throws Exception {
        var person = personService.findById(personId);
        if (person.isPresent()) {
            Set<Role> roles = person.get().getRoles();
            this.mockMvc.perform(
                    get(API_BASE + "/" + personId + "/role/")
                            .with(user(userDetails)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(MAPPER.writeValueAsString(roles), false)
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }


    @Test
    public void testRoleExistsSuccessfully() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "/" + personId + "/role/" + roleId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId)
                );
    }

    @Test
    public void testRoleNotFoundSuccessfully() throws Exception {
        this.mockMvc.perform(
                get(API_BASE + "/" + personId + "/role/" + "0")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().is4xxClientError()
                );
    }


    @Test
    public void testCreateRoleSuccessfully() throws Exception {
        var person = personService.findById(personId);

        if (person.isPresent()) {
            Role role = new Role(RoleType.ROLE_USER);
            person.get().addRole(role);
            mockMvc.perform(
                    post(API_BASE + "/" + personId + "/role/")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(role)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.roleType").value(RoleType.ROLE_USER.toString())
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testUpdateRoleSuccessfully() throws Exception {
        role.setRoleType(RoleType.ROLE_USER);

        mockMvc.perform(
                put(API_BASE + "/" + personId + "/role/")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(role)))
                .andExpect(status().isOk()
                );
        Optional<Role> optionalRole = roleService.findById(roleId);
        if (optionalRole.isPresent()) {
            Assertions.assertEquals(RoleType.ROLE_USER, optionalRole.get().getRoleType());
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    public void testDeleteRoleSuccessfully() throws Exception {
        mockMvc.perform(
                delete(API_BASE + "/" + personId + "/role/" + roleId)
                        .with(user(userDetails)))
                .andExpect(status().isOk()
                );
        var role = roleService.findById(roleId);
        Assertions.assertFalse(role.isPresent());
    }


    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllRolesNotSuccessful() throws Exception {
        var person = personService.findById(personId);
        if (person.isPresent()) {
            Set<Role> roles = person.get().getRoles();
            this.mockMvc.perform(
                            get(API_BASE + "/" + personId + "/role/")
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden()
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }


    @Test
    @WithMockUser(roles = "USER")
    public void testRoleExistsNotSuccessful() throws Exception {
        this.mockMvc.perform(
                        get(API_BASE + "/" + personId + "/role/" + roleId)
                )
                .andDo(print())
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testRoleNotFoundNotSuccessful() throws Exception {
        this.mockMvc.perform(
                        get(API_BASE + "/" + personId + "/role/" + "0")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError()
                );
    }


    @Test
    @WithMockUser(roles = "USER")
    public void testCreateRoleNotSuccessful() throws Exception {
        var person = personService.findById(personId);

        if (person.isPresent()) {
            Role role = new Role(RoleType.ROLE_USER);
            person.get().addRole(role);
            mockMvc.perform(
                            post(API_BASE + "/" + personId + "/role/")

                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(MAPPER.writeValueAsString(role)))
                    .andDo(print())
                    .andExpect(status().isForbidden()
                    );
        } else {
            throw new RuntimeException("Test failed");
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateRoleNotSuccessful() throws Exception {
        role.setRoleType(RoleType.ROLE_USER);

        mockMvc.perform(
                        put(API_BASE + "/" + personId + "/role/")

                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(role)))
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteRoleNotSuccessful() throws Exception {
        mockMvc.perform(
                        delete(API_BASE + "/" + personId + "/role/" + roleId)
                )
                .andExpect(status().isForbidden()
                );
    }

}
