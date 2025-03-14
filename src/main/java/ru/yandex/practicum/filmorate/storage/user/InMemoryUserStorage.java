package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final InMemoryFriendshipStorage friendshipStorage;

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getById(long id) {
        return users.get(id) == null ? Optional.empty() : Optional.of(users.get(id));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> getByLogin(String login) {
        return users.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findFriendsById(long id) {
        return friendshipStorage
                .findByUserId(id)
                .stream()
                .map(friendship -> getById(friendship.getFriendId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<User> findCommonFriends(long id, long otherId) {
        List<User> friends = findFriendsById(id);
        List<User> otherFriends = findFriendsById(otherId);
        return friends.stream().filter(otherFriends::contains).toList();
    }

    @Override
    public void clear() {
        users.clear();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}
