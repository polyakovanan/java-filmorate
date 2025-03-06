package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> getById(long id);

    List<Film> getPopular(int count);

    List<Film> getCommon(long userId, long friendId);

    Film create(Film film);

    Film update(Film film);

    void clear();

    void delete(long filmId);
}
