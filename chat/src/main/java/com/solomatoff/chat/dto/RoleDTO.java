package com.solomatoff.chat.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public enum RoleDTO {
    /* enum без значений */;

    private interface Id { @Positive Long getId(); }
    private interface RoleType { @NotBlank com.solomatoff.chat.domain.RoleType getRoleType(); }
    private interface PersonId { @Positive Long getPersonId(); }
    private interface Person { com.solomatoff.chat.domain.Person getPerson(); }

    public enum Request {
        /* enum без значений */;

        @Value
        public static class Create implements Id, RoleType {
            Long id;
            com.solomatoff.chat.domain.RoleType roleType;
        }
    }

    public enum Response {
        /* enum без значений */;

        @Value
        public static class Public implements Id, RoleType, PersonId {
            Long id;
            com.solomatoff.chat.domain.RoleType roleType;
            Long personId;
        }

        @Value
        public static class Private implements Id, RoleType, RoleDTO.Person {
            Long id;
            com.solomatoff.chat.domain.RoleType roleType;
            com.solomatoff.chat.domain.Person person;
        }
    }
}