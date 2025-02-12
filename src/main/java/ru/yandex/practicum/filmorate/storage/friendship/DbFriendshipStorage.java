package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("dbFriendshipStorage")
public class DbFriendshipStorage implements FriendshipStorage{
    @Override
    public Set<Long> getFriendsByUserId(long id) {
        return Set.of();
    }

    @Override
    public void create(long userId, long friendId) {

    }

    @Override
    public void accept(long userId, long friendId) {

    }

    @Override
    public void remove(long userId, long friendId) {

    }

    @Override
    public void clear() {

    }
}
