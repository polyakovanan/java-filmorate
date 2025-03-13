package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    static final String NOT_FOUND_MESSAGE = "Пользователь с id = %s не найден";
    final UserStorage userStorage;
    final FriendshipStorage friendshipStorage;
    final EventStorage eventStorage;

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User findById(Long id) {
        Optional<User> user = userStorage.getById(id);
        return user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
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

        Optional<User> userOptional = userStorage.getById(user.getId());
        userOptional.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, user.getId())));

        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            log.warn("Не указано имя пользователя. Приравниваем его к логину");
            user.setName(user.getName());
        }
        User currentUser = userStorage.update(user);
        log.info("Пользователь обновлен");
        log.debug(currentUser.toString());
        return currentUser;
    }

    public List<User> findFriends(Long id) {
        Optional<User> user = userStorage.getById(id);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        return userStorage.findFriendsById(id);
    }

    public void addFriend(Long id, Long friendId) {
        Optional<User> user = userStorage.getById(id);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        Optional<User> friend = userStorage.getById(friendId);
        friend.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, friendId)));
        friendshipStorage.create(id, friendId);
        log.info("Пользователь с id = {} добавил друга с id = {}", id, friendId);
        eventStorage.create(id, friendId, EventType.FRIEND, EventOperation.ADD);
    }

    public void removeFriend(Long id, Long friendId) {
        Optional<User> user = userStorage.getById(id);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        Optional<User> friend = userStorage.getById(friendId);
        friend.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, friendId)));
        friendshipStorage.remove(id, friendId);
        log.info("Пользователь с id = {} удалил друга с id = {}", id, friendId);
        eventStorage.create(id, friendId, EventType.FRIEND, EventOperation.REMOVE);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        Optional<User> user = userStorage.getById(id);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        Optional<User> otherUser = userStorage.getById(otherId);
        otherUser.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, otherId)));
        return userStorage.findCommonFriends(id, otherId);
    }

    public List<Event> findFeed(Long id) {
        Optional<User> user = userStorage.getById(id);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        return eventStorage.findByUserId(id);
    }

    private void validate(User user) throws DuplicatedDataException {
        Optional<User> userByEmail = userStorage.getByEmail(user.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(user.getId())) {
            log.error("Этот email уже используется");
            throw new DuplicatedDataException("Этот email уже используется");
        }

        Optional<User> userByLogin = userStorage.getByLogin(user.getLogin());
        if (userByLogin.isPresent() && !userByLogin.get().getId().equals(user.getId())) {
            log.error("Этот логин уже используется");
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Логин {} уже содержит пробелы", user.getLogin());
            throw new ValidationException("login", "Логин не может содержать пробелы");
        }
    }

    public void deleteUser(long userId) {
        Optional<User> user = userStorage.getById(userId);
        user.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));
        userStorage.delete(userId);
    }
}
