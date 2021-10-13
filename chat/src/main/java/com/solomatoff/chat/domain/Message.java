package com.solomatoff.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Entity
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(
            max = 255,
            message = "Message text must be maximum 255 characters."
    )
    @NotBlank(message = "Message text must be not empty")
    private String messageText;

    @JsonIgnoreProperties({"password", "roles", "rooms"})
    @ManyToOne
    private Person author;

    @JsonIgnoreProperties({"description", "owner", "messages"})
    @ManyToOne
    private Room room;

    protected Message() {
    }

    public Message(String messageText, Person author, Room room) {
        if (messageText == null || messageText.isEmpty()) {
            throw new NullPointerException("Can't add null message text");
        }
        this.messageText = messageText;
        if (author == null) {
            throw new IllegalStateException("Message must be assigned to the Author");
        }
        if (room == null) {
            throw new IllegalStateException("Message must be assigned to the Room");
        } else {
            this.room = room;
            room.addMessage(this, author);
        }
    }

    public Long getId() {
        return id;
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String text) {
        this.messageText = text;
    }


    public Person getAuthor() {
        return author;
    }
    public void setAuthor(Person author) {
        this.author = author;
    }

    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) {
            return false;
        }
        if (messageText != null ? !messageText.equals(message.messageText) : message.messageText != null) {
            return false;
        }
        if (author != null ? !author.equals(message.author) : message.author != null) {
            return false;
        }
        return room != null ? room.equals(message.room) : message.room == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (messageText != null ? messageText.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id
                + ",\"messageText\":\"" + messageText + '\"'
                + ",\"author\":" + (author.getId() != null ? author.getId() : null)
                + ",\"room\":" + (room.getId() != null ? room.getId() : null)
                + '}';
    }
}
