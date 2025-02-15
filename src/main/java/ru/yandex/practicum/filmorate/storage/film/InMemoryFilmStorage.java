package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.InMemoryMPARatingStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final InMemoryLikeStorage likeStorage;
    private final InMemoryGenreStorage genreStorage;
    private final InMemoryMPARatingStorage mpaRatingStorage;

    @Override
    public List<Film> getAll() {
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> getById(long id) {
        return films.get(id) == null ? Optional.empty() : Optional.of(films.get(id));
    }

    @Override
    public List<Film> getPopular(int count) {
        return likeStorage.findAll().stream()
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.counting())).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        addRefNames(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        addRefNames(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void clear() {
        films.clear();
    }

    private void addRefNames(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        film.getGenres().
                forEach(g -> {
                    Optional<Genre> genre = genreStorage.getById(g.getId());
                    genre.ifPresent(value -> g.setName(value.getName()));
                });
        if (film.getMpa() != null) {
            Optional<MPARating> mpaRating = mpaRatingStorage.getById(film.getMpa().getId());
            mpaRating.ifPresent(value -> film.getMpa().setName(value.getName()));
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
