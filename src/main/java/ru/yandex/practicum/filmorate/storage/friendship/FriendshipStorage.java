package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Set;

public interface FriendshipStorage {
    Set<Long> getFriendsByUserId(long id);

    void create(long userId, long friendId);

    void accept(long userId, long friendId);

    void remove(long userId, long friendId);

    void clear();
}
