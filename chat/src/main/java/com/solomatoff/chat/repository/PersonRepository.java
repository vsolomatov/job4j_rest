package com.solomatoff.chat.repository;

import com.solomatoff.chat.domain.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Long> {

    @Query("SELECT distinct a FROM Person a "
                + "LEFT JOIN FETCH a.rooms b "
                    + "LEFT JOIN FETCH b.messages "
                + "LEFT JOIN FETCH a.roles c "
            + "ORDER BY a.id")
    Iterable<Person> findAll();

    @Query("SELECT distinct a FROM Person a "
                + "LEFT JOIN FETCH a.rooms b "
                    + "LEFT JOIN FETCH b.messages "
                + "LEFT JOIN FETCH a.roles c "
            + "WHERE a.id = ?1")
    Optional<Person> findById(long id);

    @Query("SELECT distinct a FROM Person a "
            + "LEFT JOIN FETCH a.roles b "
            + "WHERE a.login = ?1")
    Optional<Person> findByLogin(String login);
}