package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.store.IPersonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class PersonService implements IPersonService {
    @Autowired
    @Qualifier("personStore")
    private IPersonStore personStore;

    @Transactional
    @Override
    public Optional<Person> saveOrUpdate(Person person) {
        return personStore.saveOrUpdate(person);
    }

    @Override
    public Collection<Person> findAll() {
        return personStore.findAll();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personStore.findById(id);
    }

    @Override
    public void delete(Person person) {
        personStore.delete(person);
    }

    @Override
    public Optional<Person> findByLogin(String login) {
        return personStore.findByLogin(login);
    }

}
