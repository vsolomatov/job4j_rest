package com.solomatoff.bank.controller;

import com.solomatoff.bank.domain.Address;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ExampleController {

    private final Map<Integer, Address> addressService = new HashMap<>(Map.ofEntries(
            Map.entry(1, new Address(
                    1, "Russia", "Moscow", "Gogolya", "5a"
            )),
            Map.entry(2, new Address(
                    2, "Russia", "St. Petersburg", "Dostoevskogo", "10"
            )),
            Map.entry(3, new Address(
                    3, "Russia", "Ufa", "Aksakova", "93"
            ))
    ));

    @PatchMapping("/example2")
    public Address example2(@RequestBody Address address) throws InvocationTargetException, IllegalAccessException {
        var current = addressService.get(address.getId());
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var methods = current.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method: methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid properties mapping");
                }
                var newValue = getMethod.invoke(address);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        addressService.put(address.getId(), address);
        return current;
    }

}
