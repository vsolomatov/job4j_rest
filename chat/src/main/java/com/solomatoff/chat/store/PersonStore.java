package com.solomatoff.chat.store;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class PersonStore implements IPersonStore {

    private final PersonRepository personRepository;

    public PersonStore(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Collection<Person> findAll() {
        return (Collection<Person>) personRepository.findAll();
    }


    @Override
    public Optional<Person> findById(long id) {
        return personRepository.findById(id);
    }

    @Override
    public Optional<Person> saveOrUpdate(Person person) {
        return Optional.of(personRepository.save(person));
    }

    @Override
    public void delete(Person person) {
        personRepository.delete(person);
    }


    @Override
    public Optional<Person> findByLogin(String login) {
        return personRepository.findByLogin(login);
    }
}