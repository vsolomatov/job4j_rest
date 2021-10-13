package com.solomatoff.bank.service;

import com.solomatoff.bank.domain.Address;
import com.solomatoff.bank.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Optional<Address> saveOrUpdate(Address address) {
        return Optional.of(addressRepository.saveOrUpdate(address));
    }

    @Override
    public Collection<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public Address findById(int id) {
        var address = addressRepository.findById(id);
        return address.orElse(null);

    }

    @Override
    public void delete(Address address) {
        addressRepository.delete(address.getId());
    }
}
