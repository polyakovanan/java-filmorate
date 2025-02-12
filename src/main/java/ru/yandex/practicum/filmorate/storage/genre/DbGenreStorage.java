package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component("dbGenreStorage")
public class DbGenreStorage implements GenreStorage{
    @Override
    public List<Genre> getAll() {
        return List.of();
    }

    @Override
    public Optional<Genre> getById(long id) {
        return Optional.empty();
    }
}
