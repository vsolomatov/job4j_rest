package com.solomatoff.chat.repository;

import com.solomatoff.chat.domain.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<Room, Long> {

    @Query("SELECT distinct a FROM Room a "
                + "LEFT JOIN FETCH a.messages "
            + "ORDER BY a.id")
    Iterable<Room> findAll();

    @Query("SELECT distinct a FROM Room a "
                + "LEFT JOIN FETCH a.messages "
            + "WHERE a.id = ?1")
    Optional<Room> findById(long id);
}
