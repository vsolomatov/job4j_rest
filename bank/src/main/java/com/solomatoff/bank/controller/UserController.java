package com.solomatoff.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.bank.domain.Address;
import com.solomatoff.bank.domain.User;
import com.solomatoff.bank.dto.UserDTO;
import com.solomatoff.bank.service.AddressService;
import com.solomatoff.bank.service.BankService;
import com.solomatoff.bank.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class.getSimpleName());

    private final BankService bankService;
    private final UserService userService;
    private final AddressService addressService;

    private final ObjectMapper objectMapper;

    public UserController(BankService bankService, UserService userService, AddressService addressService, ObjectMapper objectMapper) {
        this.bankService = bankService;
        this.userService = userService;
        this.addressService = addressService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public User save(@RequestBody Map<String, String> body) {
        var username = body.get("username");
        var password = body.get("password");
        if (username == null || password == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        var user = new User(username, password);
        bankService.addUser(user);
        return user;
    }

    @GetMapping
    public User findByPassport(@RequestParam String password) {
        return bankService.findByPassport(password).orElse(null);
    }


    @PostMapping("/example1")
    public User example1(@RequestBody UserDTO userDTO) {
        Address address = addressService.findById(userDTO.getAddressId());
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var user = new User(userDTO.getName(), userDTO.getSurname());
        user.setAddress(address);
        var optionalUser = userService.saveOrUpdate(user);
        return optionalUser.orElse(null);

    }


    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

}