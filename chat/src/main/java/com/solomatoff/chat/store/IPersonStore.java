package com.solomatoff.chat.store;

import com.solomatoff.chat.domain.Person;

import java.util.Optional;

public interface IPersonStore extends IStore<Person> {

    Optional<Person> findByLogin(String login);

}
