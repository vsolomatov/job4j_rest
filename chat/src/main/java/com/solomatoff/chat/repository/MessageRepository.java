package com.solomatoff.chat.repository;

import com.solomatoff.chat.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
