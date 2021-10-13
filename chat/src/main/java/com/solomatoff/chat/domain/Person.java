package com.solomatoff.chat.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Login must be non null")
    @Size(
            min = 3,
            max = 50,
            message = "Login is required, minimum 3, maximum 50 characters."
    )
    private String login;

    @NotNull(message = "Password must be non null")
    @Size(
            min = 3,
            max = 255,
            message = "Password is required, minimum 3, maximum 255 characters."
    )
    private String password;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Room> rooms = new HashSet<>();


    protected Person() {
    }

    public Person(String login, String password) {
        if (login == null || login.isEmpty()) {
            throw new IllegalStateException("Can't add null login");
        }
        this.login = login;
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("Can't add null password");
        }
        this.password = password;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String username) {
        this.login = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Room> getRooms() {
        return Collections.unmodifiableSet(rooms);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /*
        Methods for role
     */
    public void addRole(Role role) {
        if (role == null) {
            throw new NullPointerException("Can't add null Role");
        }
        if (role.getRoleType() == null) {
            throw new NullPointerException("Can't add Role with null name");
        }
        roles.add(role);
        role.setPerson(this);
    }

    public void addRoles(Set<RoleType> roleTypes) {
        if (roleTypes == null || roleTypes.size() == 0) {
            addRole(new Role(RoleType.ROLE_USER));
        } else {
            for (RoleType roleType: roleTypes) {
                addRole(new Role(roleType));
            }
        }
    }

    public Role readRole(Long id) {
        List<Role> listRoles = this.roles.stream().filter(person -> person.getId().equals(id)).collect(Collectors.toList());
        return (listRoles.size() == 1) ? listRoles.get(0) : null;
    }

    public void updateRole(Role role) {
        for (Role r : this.roles) {
            if (r.getId().equals(role.getId())) {
                r.setRoleType(role.getRoleType());
                break;
            }
        }
    }

    public void deleteRole(Role role) {
        this.roles.remove(role);
    }


    /*
        Methods for room
     */
    public void addRoom(Room room) {
        if (room == null) {
            throw new NullPointerException("Can't add null Room");
        }
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new NullPointerException("Can't add Room with null name");
        }
        rooms.add(room);
        room.setOwner(this);
    }

    public Room readRoom(Long id) {
        List<Room> listRooms = this.rooms.stream().filter(person -> person.getId().equals(id)).collect(Collectors.toList());
        return (listRooms.size() == 1) ? listRooms.get(0) : null;
    }

    public void updateRoom(Room room) {
        for (Room r : this.rooms) {
            if (r.getId().equals(room.getId())) {
                r.setName(room.getName());
                r.setDescription(room.getDescription());
                break;
            }
        }
    }

    public void deleteRoom(Room room) {
        this.rooms.remove(room);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) {
            return false;
        }
        if (login != null ? !login.equals(person.login) : person.login != null) {
            return false;
        }
        return password != null ? password.equals(person.password) : person.password == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id
                + ",\"login\":\"" + login + "\""
                + ",\"password\":\"" + password + "\""
                + ",\"roles\":" + roles
                + ",\"rooms\":" + rooms
                + '}';
    }
}
