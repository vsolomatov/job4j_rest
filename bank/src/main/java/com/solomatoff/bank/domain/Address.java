package com.solomatoff.bank.domain;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class Address extends Id {

    int id;

    String country;

    String city;

    String street;

    String house;

}
