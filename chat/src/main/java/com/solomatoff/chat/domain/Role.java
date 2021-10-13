package com.solomatoff.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person = new Person("Created from Role", "888");


    protected Role() {
    }

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }


    public Long getId() {
        return id;
    }

    public RoleType getRoleType() {
        return roleType;
    }
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Role role = (Role) o;

        if (id != null ? !id.equals(role.id) : role.id != null) {
            return false;
        }
        if (roleType != role.roleType) {
            return false;
        }
        return person != null ? person.equals(role.person) : role.person == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id
                + ",\"roleType\":\"" + roleType + "\""
                + ", \"person\":" + (person.getId() != null ? person.getId() : null)
                + '}';
    }

}
