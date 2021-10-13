package com.solomatoff.bank.service;

import com.solomatoff.bank.domain.Address;

import java.util.Collection;
import java.util.Optional;

public interface IAddressService {

        Optional<Address> saveOrUpdate(Address model);
        Collection<Address> findAll();
        Address findById(int id);
        void delete(Address model);
}
