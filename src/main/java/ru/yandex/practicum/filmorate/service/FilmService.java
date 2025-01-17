package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    final FilmStorage filmStorage;

    public List<Film> findAll() {
        return filmStorage.getAll();
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

        validate(film);

        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        if (filmOptional.isPresent()) {
            Film oldFilm = filmOptional.get();
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            oldFilm.setDuration(film.getDuration());
            log.info("Фильм успешно обновлен");
            return oldFilm;
        }
        log.error("Не найден фильм с id = {}", film.getId());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
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
            throw new ValidationException("Дата релиза не может быть раньше " +
                    Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER));
        }
    }
}
