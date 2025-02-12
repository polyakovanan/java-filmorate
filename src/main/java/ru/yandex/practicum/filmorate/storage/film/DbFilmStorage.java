package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component("dbFilmStorage")
public class DbFilmStorage implements FilmStorage{
    @Override
    public List<Film> getAll() {
        return List.of();
    }

    @Override
    public Optional<Film> getById(long id) {
        return Optional.empty();
    }

    @Override
    public Film create(Film user) {
        return null;
    }

    @Override
    public void clear() {

    }
}
