package com.solomatoff.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.domain.RoleType;
import com.solomatoff.chat.dto.PersonDTO;
import com.solomatoff.chat.exception.IllegalFieldException;
import com.solomatoff.chat.service.IPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/person")
public class V1PersonController {

    private final IPersonService personService;
    private final BCryptPasswordEncoder encoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(V1PersonController.class.getSimpleName());

    private final ObjectMapper objectMapper;

    public V1PersonController(final IPersonService personService,
                              BCryptPasswordEncoder encoder,
                              ObjectMapper objectMapper) {
        this.personService = personService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return new ArrayList<>(personService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable long id) {
        Optional<Person> optionalPerson = personService.findById(id);
        return optionalPerson
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check ID.")
                );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (person != null) {
            if (person.getRoles().size() != 0 || person.getRooms().size() != 0) {
                throw new IllegalArgumentException("The new person should not contain roles and rooms");
            }
            if (person.getId() == null) {
                // Проверим уникальность login
                var per = personService.findByLogin(person.getLogin());
                if (per.isEmpty()) {
                    // Проверим корректность логина
                    if (person.getLogin().length() < 3 || person.getLogin().length() > 50) {
                        throw new IllegalFieldException("Invalid login");
                    }
                    // Проверим корректность пароля
                    checkPassword(person.getPassword());
                    // Закодируем пароль
                    person.setPassword(encoder.encode(person.getPassword()));
                    // Добавим по дефолту роль пользователя
                    person.addRole(new Role(RoleType.ROLE_USER));
                    // Сохраняем
                    var optionalPerson = personService.saveOrUpdate(person);
                    return optionalPerson
                            .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                            .orElseThrow(
                                    () -> new IllegalArgumentException("Person is illegal")
                            );
                } else {
                    throw new IllegalArgumentException("Person login is duplicated");
                }
            } else {
                throw new IllegalArgumentException("Person ID must be empty");
            }
        } else {
            throw new IllegalArgumentException("Person mustn't be empty");
        }
    }

    private void checkPassword(String password) {
        if (password.length() < 3 || password.length() > 255) {
            throw new IllegalFieldException("Invalid password");
        }
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        var optionalPerson = personService.findById(person.getId());
        if (optionalPerson.isPresent()) {
            Person per = optionalPerson.get();
            // Проверим корректность пароля
            checkPassword(person.getPassword());
            // Позволяем изменять только password и закодируем его
            per.setPassword(encoder.encode(person.getPassword()));
            // Сохраняем
            personService.saveOrUpdate(per);
            return ResponseEntity.ok().build();
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PatchMapping("/")
    public ResponseEntity<PersonDTO.Response.Public> patch(@Valid @RequestBody PersonDTO.Request.Create person) {
        var optionalPerson = personService.findByLogin(person.getLogin());
        if (optionalPerson.isPresent()) {
            Person per = optionalPerson.get();
            // Проверим корректность пароля
            checkPassword(person.getPassword());
            // Позволяем изменять только password и закодируем его
            per.setPassword(encoder.encode(person.getPassword()));
            // Сохраняем
            optionalPerson = personService.saveOrUpdate(per);
            if (optionalPerson.isPresent()) {
                Person p = optionalPerson.get();
                PersonDTO.Response.Public personDto =
                        new PersonDTO.Response.Public(
                            p.getId(),
                            p.getLogin(),
                            p.getRoles()
                        );
                return ResponseEntity.of(Optional.of(personDto));
            } else {
                throw new IllegalArgumentException("Person is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        var person = personService.findById(id);
        if (person.isPresent()) {
            personService.delete(person.get());
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person is not found. Please, check ID."
            );
        }
    }


    @ExceptionHandler(value = { IllegalFieldException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

}