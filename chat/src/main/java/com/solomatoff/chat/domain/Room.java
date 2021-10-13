package com.solomatoff.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room name must be non null")
    @NotBlank(message = "Room name must be not empty")
    private String name;

    private String description;

    @JsonIgnoreProperties({"password", "roles", "rooms"})
    @ManyToOne
    Person owner;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Message> messages = new HashSet<>();


    protected Room() {
    }

    public Room(String name, String description, Person owner) {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException("Can't add room without name");
        }
        this.name = name;
        this.description = description;
        if (owner == null) {
            throw new IllegalStateException("Room must be assigned to the Owner");
        }
        this.owner = owner;
        owner.addRoom(this);
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Person getOwner() {
        return owner;
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    /*
        Methods for message
    */
    public void addMessage(Message message, Person author) {
        if (message == null || message.getMessageText().isEmpty()) {
            throw new NullPointerException("Can't add null Message");
        }
        messages.add(message);
        message.setRoom(this);
        message.setAuthor(author);
    }

    public Message readMessage(Long id) {
        List<Message> listMessage = this.messages.stream().filter(message -> message.getId().equals(id)).collect(Collectors.toList());
        return (listMessage.size() == 1) ? listMessage.get(0) : null;
    }

    public void updateMessage(Message message) {
        for (Message mes : this.messages) {
            if (mes.getId().equals(message.getId())) {
                // Изменяем только текст сообщения
                mes.setMessageText(message.getMessageText());
                break;
            }
        }
    }

    public void deleteMessage(Message message) {
        this.messages.remove(message);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Room room = (Room) o;

        if (id != null ? !id.equals(room.id) : room.id != null) {
            return false;
        }
        if (name != null ? !name.equals(room.name) : room.name != null) {
            return false;
        }
        if (description != null ? !description.equals(room.description) : room.description != null) {
            return false;
        }
        return owner != null ? owner.equals(room.owner) : room.owner == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id
                + ",\"name\":\"" + name + '\"'
                + ",\"description\":\"" + description + '\"'
                + ",\"owner\":" + (owner.getId() != null ? owner.getId() : null)
                + ",\"messages\":" + messages
                + '}';
    }
}
