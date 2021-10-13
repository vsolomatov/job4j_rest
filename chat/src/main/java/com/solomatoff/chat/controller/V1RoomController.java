package com.solomatoff.chat.controller;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Room;
import com.solomatoff.chat.dto.RoomDTO;
import com.solomatoff.chat.service.IPersonService;
import com.solomatoff.chat.service.IRoomService;
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
public class V1RoomController {

    private final IPersonService personService;
    private final IRoomService roomService;

    public V1RoomController(IPersonService personService, IRoomService roomService) {
        this.personService = personService;
        this.roomService = roomService;
    }


    @GetMapping("/{idPerson}/room/")
    public List<Room> findRoomsAll(@PathVariable long idPerson) {
        var person = personService.findById(idPerson);
        return person
                .map(value -> new ArrayList<>(value.getRooms()))
                .orElseGet(ArrayList::new);
    }

    @GetMapping("/{idPerson}/room/{id}")
    public ResponseEntity<Room> findRoomById(@PathVariable long idPerson, @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(id);
            if (room != null) {
                return new ResponseEntity<>(room, HttpStatus.OK);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PostMapping("/{idPerson}/room/")
    public ResponseEntity<Room> createRoom(@PathVariable long idPerson, @Valid @RequestBody Room room) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            // Добавим сущность через метод сущности Person
            person.get().addRoom(room);
            // Сохраним сущность в базе данных и получим её
            var r = roomService.saveOrUpdate(room);
            return r
                    .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                    .orElseThrow(
                            () -> new IllegalArgumentException("Room is illegal")
                    );
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PutMapping("/{idPerson}/room/")
    public ResponseEntity<Void> updateRoom(@PathVariable long idPerson, @Valid @RequestBody Room room) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Room r = per.readRoom(room.getId());
            if (r != null) {
                // Обновим сущность через метод сущности Person
                per.updateRoom(room);
                // Сохраняем сущность Person
                personService.saveOrUpdate(per);
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PatchMapping("/{idPerson}/room/")
    public ResponseEntity<RoomDTO.Response.Public> patch(@PathVariable long idPerson, @Valid @RequestBody RoomDTO.Request.Create room) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Room r = per.readRoom(room.getId());
            if (r != null) {
                r.setName(room.getName());
                r.setDescription(room.getDescription());
                // Обновим сущность через метод сущности Person
                per.updateRoom(r);
                // Сохраняем
                var optionalPerson = personService.saveOrUpdate(per);
                if (optionalPerson.isPresent()) {
                    Room room1 = optionalPerson.get().readRoom(room.getId());
                    RoomDTO.Response.Public roomDto =
                            new RoomDTO.Response.Public(
                                    room1.getId(),
                                    room1.getName(),
                                    room1.getDescription()
                            );
                    return ResponseEntity.of(Optional.of(roomDto));
                } else {
                    throw new IllegalArgumentException("Room is illegal");
                }
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @DeleteMapping("/{idPerson}/room/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable long idPerson, @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Person per = person.get();
            Room room = per.readRoom(id);
            if (room != null) {
                // Удалим сущность через метод сущности Person
                per.deleteRoom(room);
                // Сохраняем сущность Person
                personService.saveOrUpdate(per);
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room is not found. Please, check ID."
                );
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

}