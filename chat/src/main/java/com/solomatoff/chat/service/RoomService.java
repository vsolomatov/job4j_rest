package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Room;
import com.solomatoff.chat.store.IRoomStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class RoomService implements IRoomService {
    @Autowired
    @Qualifier("roomStore")
    private IRoomStore roomStore;

    @Transactional
    @Override
    public Optional<Room> saveOrUpdate(Room room) {
        return roomStore.saveOrUpdate(room);
    }

    @Override
    public Collection<Room> findAll() {
        return roomStore.findAll();
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomStore.findById(id);
    }

    @Override
    public void delete(Room room) {
        roomStore.delete(room);
    }
}
