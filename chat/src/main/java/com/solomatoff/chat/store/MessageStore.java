package com.solomatoff.chat.store;

import com.solomatoff.chat.domain.Message;
import com.solomatoff.chat.repository.MessageRepository;
import com.solomatoff.chat.repository.PersonRepository;
import com.solomatoff.chat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;


@Service
public class MessageStore implements IMessageStore {

    private final MessageRepository messageRepository;

    public MessageStore(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Collection<Message> findAll() {
        return (Collection<Message>) messageRepository.findAll();
    }

    @Override
    public Optional<Message> findById(long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Optional<Message> saveOrUpdate(Message message) {
        if (message != null) {
            return Optional.of(messageRepository.save(message));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Message person) {
        messageRepository.delete(person);
    }

}