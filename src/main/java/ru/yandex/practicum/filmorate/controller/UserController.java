package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        log.debug(users.toString());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя");
        log.debug(user.toString());

        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Не указано имя пользователя. Приравниваем его к логину");
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан");
        log.debug(user.toString());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя");
        log.debug(newUser.toString());

        if (newUser.getId() == null) {
            log.error("Не указан id пользователя");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        validate(newUser);

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setName(newUser.getName());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                log.warn("Не указано имя пользователя. Приравниваем его к логину");
                oldUser.setName(newUser.getName());
            }

            log.info("Пользователь обновлен");
            log.debug(oldUser.toString());
            return oldUser;
        }

        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private void validate(User user) throws DuplicatedDataException {
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.values()
                .stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Логин {} уже используется", user.getLogin());
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Логин {} уже содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}