package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component("dbUserStorage")
public class DbUserStorage implements UserStorage{
    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public void clear() {

    }
}
