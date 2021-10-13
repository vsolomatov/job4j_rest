package com.solomatoff.chat.controller;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Role;
import com.solomatoff.chat.dto.RoleDTO;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/person")
public class V1RoleController {

    private final IPersonService personService;
    private final IRoleService roleService;

    public V1RoleController(IPersonService personService, IRoleService roleService) {
        this.personService = personService;
        this.roleService = roleService;
    }


    @GetMapping("/{idPerson}/role/")
    public List<Role> findRolesAll(@PathVariable long idPerson) {
        var person = personService.findById(idPerson);
        return person
                .map(value -> new ArrayList<>(value.getRoles()))
                .orElseGet(ArrayList::new);
    }

    @GetMapping("/{idPerson}/role/{id}")
    public ResponseEntity<Role> findRoleById(@PathVariable long idPerson, @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Role role = person.get().readRole(id);
            if (role != null) {
                return new ResponseEntity<>(role, HttpStatus.OK);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PostMapping("/{idPerson}/role/")
    public ResponseEntity<Role> createRole(@PathVariable long idPerson, @Valid @RequestBody Role role) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            // Добавим сущность через метод сущности Person
            person.get().addRole(role);
            // Сохраним сущность в базе данных и получим её
            var r = roleService.saveOrUpdate(role);
            return r
                    .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                    .orElseThrow(
                            () -> new IllegalArgumentException("Role is illegal")
                    );
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PutMapping("/{idPerson}/role/")
    public ResponseEntity<Void> updateRole(@PathVariable long idPerson, @Valid @RequestBody Role role) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Role r = per.readRole(role.getId());
            if (r != null) {
                // Обновим сущность через метод сущности Person
                per.updateRole(role);
                // Сохраняем сущность Person
                personService.saveOrUpdate(per);
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PatchMapping("/{idPerson}/role/")
    public ResponseEntity<RoleDTO.Response.Public> patch(@PathVariable long idPerson, @Valid @RequestBody RoleDTO.Request.Create role) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Role r = per.readRole(role.getId());
            if (r != null) {
                // Обновим сущность через метод сущности Person
                r.setRoleType(role.getRoleType());
                per.updateRole(r);
                // Сохраняем
                var optionalPerson = personService.saveOrUpdate(per);
                if (optionalPerson.isPresent()) {
                    Role role1 = optionalPerson.get().readRole(role.getId());
                    RoleDTO.Response.Public roleDto =
                            new RoleDTO.Response.Public(
                                    role1.getId(),
                                    role1.getRoleType(),
                                    role1.getPerson().getId()
                            );
                    return ResponseEntity.of(Optional.of(roleDto));
                } else {
                    throw new IllegalArgumentException("Role is illegal");
                }
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @DeleteMapping("/{idPerson}/role/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable long idPerson, @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Role role = per.readRole(id);
            if (role != null) {
                // Удалим сущность через метод сущности Person
                per.deleteRole(role);
                // Сохраняем сущность Person
                personService.saveOrUpdate(per);
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

}