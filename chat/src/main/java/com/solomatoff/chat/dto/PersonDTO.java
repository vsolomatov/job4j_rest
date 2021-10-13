package com.solomatoff.chat.dto;

import com.solomatoff.chat.domain.Role;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Set;

public enum PersonDTO {
    /* enum без значений */;

    private interface Id { @Positive Long getId(); }
    private interface Login { @NotBlank String getLogin(); }
    private interface Password { @NotBlank String getPassword(); }
    private interface Roles { Set<Role> getRoles(); }

    public enum Request {
        /* enum без значений */;

        @Value
        public static class Create implements Login, Password {
            String login;
            String password;
        }
    }

    public enum Response {
        /* enum без значений */;

        @Value
        public static class Public implements Id, Login, Roles {
            Long id;
            String login;
            Set<Role> roles;
        }

        @Value
        public static class Private implements Id, Login, Password, Roles {
            Long id;
            String login;
            String password;
            Set<Role> roles;
        }
    }
}