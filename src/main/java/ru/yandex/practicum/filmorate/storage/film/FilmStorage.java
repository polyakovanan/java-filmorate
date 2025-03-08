package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> getById(long id);

    List<Film> findPopular(int count, Integer genreId, Integer year);

    List<Film> getCommon(long userId, long friendId);

    List<Film> getByDirector(Long directorId, SortBy sortBy);

    Film create(Film film);

    Film update(Film film);

    void clear();

    void delete(long filmId);
}
