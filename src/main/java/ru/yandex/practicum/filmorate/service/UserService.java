package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Не указано имя пользователя. Приравниваем его к логину");
            user.setName(user.getLogin());
        }

        User createdUser = userStorage.create(user);
        log.info("Пользователь создан");
        log.debug(createdUser.toString());
        return createdUser;
    }

    public User update(User user) {
        if (user.getId() == null) {
            log.error("Не указан id пользователя");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        validate(user);

        Optional<User> userOptional = userStorage.getById(user.getId());

        if (userOptional.isPresent()) {
            User oldUser = userOptional.get();
            oldUser.setLogin(user.getLogin());
            oldUser.setEmail(user.getEmail());
            oldUser.setBirthday(user.getBirthday());
            oldUser.setName(user.getName());
            if (user.getName() == null || user.getName().isBlank()) {
                oldUser.setName(user.getLogin());
            } else {
                log.warn("Не указано имя пользователя. Приравниваем его к логину");
                oldUser.setName(user.getName());
            }

            log.info("Пользователь обновлен");
            log.debug(oldUser.toString());
            return oldUser;
        }

        log.error("Пользователь с id = {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private void validate(User user) throws DuplicatedDataException {
        List<User> users = userStorage.getAll();

        if (users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Логин {} уже используется", user.getLogin());
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Логин {} уже содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}
