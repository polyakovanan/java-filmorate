package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikeStorage {
    Set<Long> getFilmsByUserId(long id);

    Set<Long> getUsersByFilmId(long id);

    Set<Long> getPopularFilms(int count);

    void create(long userId, long filmId);

    void remove(long userId, long filmId);

    void clear();
}
