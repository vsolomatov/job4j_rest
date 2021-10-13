package com.solomatoff.chat.controller;

import com.solomatoff.chat.domain.Message;
import com.solomatoff.chat.domain.Room;
import com.solomatoff.chat.dto.MessageDTO;
import com.solomatoff.chat.service.IMessageService;
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
public class V1MessageController {

    private final IPersonService personService;
    private final IRoomService roomService;
    private final IMessageService messageService;

    public V1MessageController(final IPersonService personService,
                               final IRoomService roomService,
                               IMessageService messageService) {
        this.personService = personService;
        this.roomService = roomService;
        this.messageService = messageService;
    }


    @GetMapping("/{idPerson}/room/{idRoom}/message/")
    public ResponseEntity<List<Message>> findMessagesAll(@PathVariable long idPerson,
                                                        @PathVariable long idRoom) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                return new ResponseEntity<>(new ArrayList<>(room.getMessages()), HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }


    @GetMapping("/{idPerson}/room/{idRoom}/message/{id}")
    public ResponseEntity<Message> findMessageById(@PathVariable long idPerson,
                                                   @PathVariable long idRoom,
                                                   @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                var message = room.getMessages()
                        .stream().filter(m -> m.getId() == id).findFirst();
                return message.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Message is not found. Please, check ID.")
                        );
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PostMapping("/{idPerson}/room/{idRoom}/message/")
    public ResponseEntity<Message> createMessage(@PathVariable long idPerson,
                                                 @PathVariable long idRoom,
                                                 @Valid @RequestBody Message message) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                // Добавим сущность через метод сущности Room
                room.addMessage(message, person.get());
                // Сохраним сущность
                var optionalMessage = messageService.saveOrUpdate(message);
                return optionalMessage
                        .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                        .orElseThrow(
                                () -> new IllegalArgumentException("Message is illegal")
                        );
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PutMapping("/{idPerson}/room/{idRoom}/message/")
    public ResponseEntity<Void> updateMessage(@PathVariable long idPerson,
                                              @PathVariable long idRoom,
                                              @Valid @RequestBody Message message) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                Message messageFromDB = room.readMessage(message.getId());
                if (messageFromDB != null) {
                    // Обновим сущность через метод сущности Room
                    room.updateMessage(message);
                    // Обновим сущность Room в базе данных
                    roomService.saveOrUpdate(room);
                    return ResponseEntity.ok().build();
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Message is not found. Please, check ID."
                    );
                }
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @PatchMapping("/{idPerson}/room/{idRoom}/message/")
    public ResponseEntity<MessageDTO.Response.Public> patch(@PathVariable long idPerson,
                                                            @PathVariable long idRoom,
                                                            @Valid @RequestBody MessageDTO.Request.Create message) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                Message m = room.readMessage(idRoom);
                if (m != null) {
                    m.setMessageText(message.getMessageText());
                    // Обновим сущность через метод сущности Room
                    room.updateMessage(m);
                    // Сохраняем
                    var optionalRoom = roomService.saveOrUpdate(room);
                    if (optionalRoom.isPresent()) {
                        Message message1 = optionalRoom.get().readMessage(message.getId());
                        MessageDTO.Response.Public messageDto =
                                new MessageDTO.Response.Public(
                                        message1.getId(),
                                        message1.getMessageText()
                                );
                        return ResponseEntity.of(Optional.of(messageDto));
                    } else {
                        throw new IllegalArgumentException("Message is illegal");
                    }
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Message is not found. Please, check ID."
                    );
                }
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

    @DeleteMapping("/{idPerson}/room/{idRoom}/message/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable long idPerson,
                                              @PathVariable long idRoom,
                                              @PathVariable long id) {
        var person = personService.findById(idPerson);
        if (person.isPresent()) {
            Room room = person.get().readRoom(idRoom);
            if (room != null) {
                Message messageFromDB = room.readMessage(id);
                if (messageFromDB != null) {
                    // Удалим сущность через метод сущности Room
                    room.deleteMessage(messageFromDB);
                    // Обновим сущность Room в базе данных
                    roomService.saveOrUpdate(room);
                    return ResponseEntity.ok().build();
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Message is not found. Please, check ID."
                    );
                }
            } else {
                throw new IllegalArgumentException("Room ID is illegal");
            }
        } else {
            throw new IllegalArgumentException("Person ID is illegal");
        }
    }

}