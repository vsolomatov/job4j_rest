package com.solomatoff.chat.dto;

import com.solomatoff.chat.domain.Message;
import com.solomatoff.chat.domain.Person;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

public enum RoomDTO {
    /* enum без значений */;

    private interface Id { @Positive Long getId(); }
    private interface Name { @NotNull @NotBlank String getName(); }
    private interface Description { String getDescription(); }
    private interface Owner { Person getOwner(); }
    private interface Messages { Set<Message> getMessages(); }

    public enum Request {
        /* enum без значений */;

        @Value
        public static class Create implements Id, Name, Description {
            Long id;
            String name;
            String description;
        }
    }

    public enum Response {
        /* enum без значений */;

        @Value
        public static class Public implements Id, Name, Description {
            Long id;
            String name;
            String description;
        }

        @Value
        public static class Private implements Id, Name, Description, Owner, Messages {
            Long id;
            String name;
            String description;
            Person owner;
            Set<Message> messages;
        }

    }
}