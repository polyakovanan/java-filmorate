package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.InMemoryDirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.InMemoryMPARatingStorage;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final InMemoryLikeStorage likeStorage;
    private final InMemoryGenreStorage genreStorage;
    private final InMemoryMPARatingStorage mpaRatingStorage;
    private final InMemoryDirectorStorage directorStorage;

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
        Map<Long, Long> likedFilms = new HashMap<>();
        films.values().forEach(film -> likedFilms.put(film.getId(), 0L));
        likedFilms.putAll(likeStorage.findAll().stream()
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.counting())));

        return likedFilms.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        List<Long> commonFilms = likeStorage.findAll()
                .stream()
                .filter(l -> l.getUserId() == userId || l.getUserId() == friendId)
                .map(Like::getFilmId)
                .toList();

        Map<Long, Long> likedFilms = new HashMap<>();
        films.values()
                .stream()
                .filter(f -> commonFilms.contains(f.getId()))
                .forEach(film -> likedFilms.put(film.getId(), 0L));

        likedFilms.putAll(likeStorage.findAll().stream().filter(l -> commonFilms.contains(l.getFilmId()))
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.counting())));

        return likedFilms.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<Film> getByDirector(Long directorId, SortBy sortBy) {
        List<Film> directorFilms = (films.values().stream()
                .filter(film -> film.getDirectors().stream().anyMatch(d -> Objects.equals(d.getId(), directorId)))
                .toList());
        return switch (sortBy) {
            case LIKES -> directorFilms.stream()
                    .sorted(Comparator.comparingLong(f -> likeStorage.findCountByFilmId(f.getId())))
                    .toList().reversed();
            case YEAR -> directorFilms.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .toList();
        };
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
        film.getGenres()
                .forEach(g -> {
                    Optional<Genre> genre = genreStorage.getById(g.getId());
                    genre.ifPresent(value -> g.setName(value.getName()));
                });

        if (film.getDirectors() == null) {
            film.setDirectors(new ArrayList<>());
        }
        film.getDirectors()
                .forEach(d -> {
                    Optional<Director> director = directorStorage.getById(d.getId());
                    director.ifPresent(value -> d.setName(value.getName()));
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
