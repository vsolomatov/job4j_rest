package com.solomatoff.chat.dto;

import com.solomatoff.chat.domain.Message;
import com.solomatoff.chat.domain.Person;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Set;

public enum MessageDTO {
    /* enum без значений */;

    private interface Id { @Positive Long getId(); }
    private interface MessageText { @NotBlank String getMessageText(); }
    private interface Author { Person getAuthor(); }
    private interface Room { com.solomatoff.chat.domain.Room getRoom(); }

    public enum Request {
        /* enum без значений */;

        @Value
        public static class Create implements Id, MessageText {
            Long id;
            String messageText;
        }
    }

    public enum Response {
        /* enum без значений */;

        @Value
        public static class Public implements Id, MessageText {
            Long id;
            String messageText;
        }

        @Value
        public static class Private implements Id, MessageText, Author, MessageDTO.Room {
            Long id;
            String messageText;
            Person author;
            com.solomatoff.chat.domain.Room room;
            Set<Message> messages;
        }

    }
}