package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    static final String NOT_FOUND_MESSAGE = "Фильм с id = %s не найден";
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final MPARatingStorage mpaRatingStorage;
    final GenreStorage genreStorage;
    final LikeStorage likeStorage;
    final EventStorage eventStorage;
    final DirectorStorage directorStorage;

    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(Long id) {
        Optional<Film> film = filmStorage.getById(id);
        return film.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    public Film create(Film film) {
        validate(film);
        Film createdFilm = filmStorage.create(film);
        log.info("Фильм создан");
        log.debug(createdFilm.toString());
        return createdFilm;
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.error("Не указан id фильма");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Optional<Film> filmOptional = filmStorage.getById(film.getId());
        filmOptional.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, film.getId())));
        validate(film);
        Film currentFilm = filmStorage.update(film);
        log.info("Фильм успешно обновлен");
        return currentFilm;
    }

    public void addLike(Long id, Long userId) {
        Optional<Film> film = filmStorage.getById(id);
        film.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        Optional<User> user = userStorage.getById(userId);
        user.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        likeStorage.create(userId, id);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, id);
        eventStorage.create(userId, id, EventType.LIKE, EventOperation.ADD);
    }

    public void removeLike(Long id, Long userId) {
        Optional<Film> film = filmStorage.getById(id);
        film.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        Optional<User> user = userStorage.getById(userId);
        user.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        likeStorage.remove(userId, id);
        log.info("Пользователь с id = {} убрал лайк с фильма с id = {}", userId, id);
        eventStorage.create(userId, id, EventType.LIKE, EventOperation.REMOVE);
    }

    private void validate(Film film) throws DuplicatedDataException, ValidationException {
        Optional<Film> filmOptional = filmStorage.findDuplicate(film);
        if (filmOptional.isPresent() && !filmOptional.get().getId().equals(film.getId())) {
            log.error("Фильм с таким названием и датой релиза уже существует");
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }

        if (film.getReleaseDate().isBefore(Film.CINEMA_BIRTH_DAY)) {
            log.error("Дата релиза раньше {}", Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
            throw new ValidationException("releaseDate", "Дата релиза не может быть раньше " +
                    Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
        }

        if (film.getMpa() != null) {
            Optional<MPARating> rating = mpaRatingStorage.getById(film.getMpa().getId());
            if (rating.isEmpty()) {
                log.error("Рейтинг с id = {} не найден", film.getMpa().getId());
                throw new NotFoundException("Рейтинг с id = " + film.getMpa().getId() + " не найден");
            }
        }

        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }

        if (!film.getGenres().isEmpty()) {
            film.setGenres(film.getGenres().stream().distinct().toList());
            List<Genre> absentGenres = film.getGenres().stream()
                    .filter(g -> genreStorage.getById(g.getId()).isEmpty())
                    .toList();
            if (!absentGenres.isEmpty()) {
                log.error("Фильм содержит жанры, которых нет в базе c id = {}", absentGenres);
                throw new NotFoundException("Фильм содержит жанры, которых нет в базе c id = " + absentGenres);
            }
        }

        if (film.getDirectors() == null) {
            film.setDirectors(new ArrayList<>());
        }

        if (!film.getDirectors().isEmpty()) {
            film.setDirectors(film.getDirectors().stream().distinct().toList());
            List<Director> absentDirectors = film.getDirectors().stream()
                    .filter(d -> directorStorage.getById(d.getId()).isEmpty())
                    .toList();
            if (!absentDirectors.isEmpty()) {
                log.error("Фильм содержит режиссеров, которых нет в базе c id = {}", absentDirectors);
                throw new NotFoundException("Фильм содержит режиссеров, которых нет в базе c id = " + absentDirectors);
            }
        }
    }

    public List<Film> findPopular(Integer count, Integer year, Long genreId) {
        return filmStorage.findPopular(count, year, genreId);
    }

    public List<Film> findCommon(Long userId, Long friendId) {
        Optional<User> user = userStorage.getById(userId);
        user.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Optional<User> friend = userStorage.getById(friendId);
        friend.orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        return filmStorage.getCommon(userId, friendId);
    }

    public void deleteFilm(long filmId) {
        Optional<Film> film = filmStorage.getById(filmId);
        film.orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        filmStorage.delete(filmId);
    }

    public List<Film> findByDirector(Long directorId, SortBy sortBy) {
        Optional<Director> director = directorStorage.getById(directorId);
        director.orElseThrow(() -> new NotFoundException("Режиссер с id = " + directorId + " не найден"));
        return filmStorage.getByDirector(directorId, sortBy);
    }

    public List<Film> search(String query, SearchBy[] by) {
        return filmStorage.search(query, by);
    }
}

