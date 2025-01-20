package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    static final String NOT_FOUND_MESSAGE = "Фильм с id = %s не найден";
    final FilmStorage filmStorage;
    final UserStorage userStorage;

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
            Film oldFilm = filmOptional.get();
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            oldFilm.setDuration(film.getDuration());
            log.info("Фильм успешно обновлен");
            return oldFilm;
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
                film.get().addLike(userId);
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
                film.get().removeLike(userId);
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
    }

    public List<Film> findPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(count)
                .toList();
    }
}
