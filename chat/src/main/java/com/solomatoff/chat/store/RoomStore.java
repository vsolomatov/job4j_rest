package com.solomatoff.chat.store;

import com.solomatoff.chat.domain.Room;
import com.solomatoff.chat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class RoomStore implements IRoomStore {

    private final RoomRepository roomRepository;

    public RoomStore(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Collection<Room> findAll() {
        return (Collection<Room>) roomRepository.findAll();
    }

    @Override
    public Optional<Room> findById(long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Optional<Room> saveOrUpdate(Room room) {
        return Optional.of(roomRepository.save(room));
    }

    @Override
    public void delete(Room room) {
        roomRepository.delete(room);
    }

}