package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Message;
import com.solomatoff.chat.store.IMessageStore;
import com.solomatoff.chat.store.IPersonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class MessageService implements IMessageService {

    @Autowired
    @Qualifier("messageStore")
    private IMessageStore messageStore;


    @Override
    public Optional<Message> saveOrUpdate(Message message) {
        return messageStore.saveOrUpdate(message);
    }

    @Override
    public Collection<Message> findAll() {
        return messageStore.findAll();
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageStore.findById(id);
    }

    @Override
    public void delete(Message message) {
        messageStore.delete(message);
    }

}
