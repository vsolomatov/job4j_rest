package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Person;

import java.util.Optional;

public interface IPersonService extends IService<Person, Long> {

    Optional<Person> findByLogin(String login);

}
