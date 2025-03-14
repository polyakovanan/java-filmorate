package ru.yandex.practicum.filmorate.storage.friendship;


import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendshipStorage {
    List<Friendship> findAll();

    List<Friendship> findByUserId(long userId);

    void create(long userId, long friendId);

    void remove(long userId, long friendId);

    void clear();
}
