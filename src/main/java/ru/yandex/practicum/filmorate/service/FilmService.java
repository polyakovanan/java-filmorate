package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;

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

    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(Long id) {
        Optional<Film> film = filmStorage.getById(id);
        if (film.isPresent()) {
            return film.get();
        }
        log.error(String.format(NOT_FOUND_MESSAGE, id));
        throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
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

        if (filmOptional.isPresent()) {
            validate(film);
            Film currentFilm = filmOptional.get();
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());
            currentFilm.setReleaseDate(film.getReleaseDate());
            currentFilm.setDuration(film.getDuration());
            log.info("Фильм успешно обновлен");
            return currentFilm;
        } else {
            log.error(String.format(NOT_FOUND_MESSAGE, film.getId()));
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, film.getId()));
        }
    }

    public void addLike(Long id, Long userId) {
        Optional<Film> film = filmStorage.getById(id);
        if (film.isPresent()) {
            Optional<User> user = userStorage.getById(userId);
            if (user.isPresent()) {
                likeStorage.create(userId, id);
                log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, id);
            } else {
                log.error("Пользователь с id = {} не найден", userId);
                throw new NotFoundException("Пользователь с id = " + userId + " не найден");
            }
        } else {
            log.error(String.format(NOT_FOUND_MESSAGE, id));
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    public void removeLike(Long id, Long userId) {
        Optional<Film> film = filmStorage.getById(id);
        if (film.isPresent()) {
            Optional<User> user = userStorage.getById(userId);
            if (user.isPresent()) {
                likeStorage.remove(userId, id);
                log.info("Пользователь с id = {} убрал лайк с фильма с id = {}", userId, id);
            } else {
                log.error("Пользователь с id = {} не найден", userId);
                throw new NotFoundException("Пользователь с id = " + userId + " не найден");
            }
        } else {
            log.error(String.format(NOT_FOUND_MESSAGE, id));
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    private void validate(Film film) throws DuplicatedDataException, ValidationException {
        List<Film> films = filmStorage.getAll();
        if (films.stream()
                .anyMatch(f -> f.getName().equals(film.getName())
                        && f.getReleaseDate().equals(film.getReleaseDate())
                        && !Objects.equals(f.getId(), film.getId()))) {
            log.error("Фильм с названием {} и датой релиза {} уже существует", film.getName(), film.getReleaseDate());
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }

        if (film.getReleaseDate().isBefore(Film.CINEMA_BIRTH_DAY)) {
            log.error("Дата релиза раньше {}", Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
            throw new ValidationException("releaseDate", "Дата релиза не может быть раньше " +
                    Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
        }

        if (film.getMpaRating() != null) {
            Optional<MPARating> rating = mpaRatingStorage.getById(film.getMpaRating());
            if (rating.isEmpty()) {
                log.error("Рейтинг с id = {} не найден", film.getMpaRating());
                throw new NotFoundException("Рейтинг с id = " + film.getMpaRating() + " не найден");
            }
        }

        if (!film.getGenres().isEmpty()) {
            List<Long> absentGenres = film.getGenres().stream()
                    .filter(g -> genreStorage.getById(g).isEmpty())
                    .toList();
            if (!absentGenres.isEmpty()) {
                log.error("Фильм содержит жанры, которых нет в базе c id = {}", absentGenres);
                throw new NotFoundException("Фильм содержит жанры, которых нет в базе c id = " + absentGenres);
            }
        }
    }

    public List<Film> findPopular(int count) {
        Set<Long> filmIds = likeStorage.getPopularFilms(count);
        List<Film> films = new ArrayList<>();
        filmIds.forEach(id -> {
            Optional<Film> film = filmStorage.getById(id);
            film.ifPresent(films::add);
        });
        return films;
    }
}
