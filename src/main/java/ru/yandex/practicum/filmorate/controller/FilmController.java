package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        log.debug(films.toString());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @Valid  Film film) {

        log.info("Запрос на создание фильма");
        log.debug(film.toString());

        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно создан");
        log.debug(film.toString());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @Valid Film newFilm) {

        log.info("Запрос на обновление фильма");
        if (newFilm.getId() == null) {
            log.error("Не указан id фильма");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        validate(newFilm);

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм успешно обновлен");
            return oldFilm;
        }
        log.error("Не найден фильм с id = {}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void validate(Film film) throws DuplicatedDataException, ValidationException {
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equals(film.getName())
                            && f.getReleaseDate().equals(film.getReleaseDate())
                            && !Objects.equals(f.getId(), film.getId()))) {
            log.error("Фильм с названием {} и датой релиза {} уже существует", film.getName(), film.getReleaseDate());
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }

        if (film.getReleaseDate().isBefore(Film.CINEMA_BIRTH_DAY)) {
            log.error("Дата релиза раньше {}", Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
            throw new ValidationException("Дата релиза не может быть раньше " +
                                            Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
        }
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}