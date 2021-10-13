package com.solomatoff.chat.service;

import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.domain.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final IPersonService personService;

    public UserDetailsServiceImpl(IPersonService personService) {
        this.personService = personService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var person = personService.findByLogin(username);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        Person user = person.get();
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getRoleType().name());
            grantedAuthorities.add(simpleGrantedAuthority);
        }
        return new User(user.getLogin(), user.getPassword(), grantedAuthorities);
    }

}
