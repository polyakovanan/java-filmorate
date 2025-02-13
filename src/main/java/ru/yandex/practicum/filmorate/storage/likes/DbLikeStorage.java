package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("dbLikeStorage")
@Primary
public class DbLikeStorage implements LikeStorage {
    @Override
    public Set<Long> getFilmsByUserId(long id) {
        return Set.of();
    }

    @Override
    public Set<Long> getUsersByFilmId(long id) {
        return Set.of();
    }

    @Override
    public Set<Long> getPopularFilms(int count) {
        return Set.of();
    }

    @Override
    public void create(long userId, long filmId) {

    }

    @Override
    public void remove(long userId, long filmId) {

    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }
}
