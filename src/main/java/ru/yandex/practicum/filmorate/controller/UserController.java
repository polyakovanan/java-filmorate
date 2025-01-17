package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя");
        log.debug(user.toString());
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя");
        log.debug(newUser.toString());
        return userService.update(newUser);
    }
}