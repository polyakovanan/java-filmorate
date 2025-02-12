package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    Map<Long, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1L, new Genre(1L, "Комедия"));
        genres.put(2L, new Genre(2L, "Боевик"));
        genres.put(3L, new Genre(3L, "Триллер"));
        genres.put(4L, new Genre(4L, "Драма"));
        genres.put(5L, new Genre(5L, "Документальный"));
    }

    @Override
    public List<Genre> getAll() {
        return genres.values().stream().toList();
    }

    @Override
    public Optional<Genre> getById(long id) {
        return genres.get(id) == null ? Optional.empty() : Optional.of(genres.get(id));
    }
}
