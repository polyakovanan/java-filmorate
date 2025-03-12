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
    public List<Film> findPopular(Integer count, Integer year, Long genreId) {
        Map<Long, Long> likedFilms = new HashMap<>();
        films.values().stream()
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .filter(film -> genreId == null ||
                        (film.getGenres() != null &&
                                film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId))))
                .forEach(film -> likedFilms.put(film.getId(), 0L));

       likedFilms.forEach((key, value) -> likedFilms.put(key, likeStorage.findCountByFilmId(key)));

        return likedFilms.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(count == null ? likedFilms.size() : count)
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
                .filter(like -> like.getUserId() == userId || like.getUserId() == friendId)
                .map(Like::getFilmId)
                .toList();

        Map<Long, Long> likedFilms = new HashMap<>();
        films.values()
                .stream()
                .filter(f -> commonFilms.contains(f.getId()))
                .forEach(film -> likedFilms.put(film.getId(), 0L));

        likedFilms.putAll(likeStorage.findAll().stream().filter(like -> commonFilms.contains(like.getFilmId()))
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
                .filter(film -> film.getDirectors().stream().anyMatch(director -> Objects.equals(director.getId(), directorId)))
                .toList());
        return switch (sortBy) {
            case LIKES -> directorFilms.stream()
                    .sorted(Comparator.comparingLong(film -> likeStorage.findCountByFilmId(film.getId())))
                    .toList().reversed();
            case YEAR -> directorFilms.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .toList();
        };
    }

    @Override
    public List<Film> search(String query, SearchBy[] searchBy) {
        return films.values().stream()
                .filter(film -> Arrays.stream(searchBy).toList().contains(SearchBy.TITLE)
                        && film.getName().toLowerCase().contains(query.toLowerCase())
                || Arrays.stream(searchBy).toList().contains(SearchBy.DIRECTOR)
                        && film.getDirectors().stream().anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase())))
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
        film.getGenres()
                .forEach(genre -> {
                    Optional<Genre> genreOptional = genreStorage.getById(genre.getId());
                    genreOptional.ifPresent(value -> genre.setName(value.getName()));
                });

        if (film.getDirectors() == null) {
            film.setDirectors(new ArrayList<>());
        }
        film.getDirectors()
                .forEach(director -> {
                    Optional<Director> directorOptional = directorStorage.getById(director.getId());
                    directorOptional.ifPresent(value -> director.setName(value.getName()));
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

    @Override
    public void delete(long filmId) {
        films.remove(filmId);
    }
}
