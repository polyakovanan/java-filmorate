package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmController.class)
class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void init() {
        filmController = new FilmController();
    }

    @Test
    void filmControllerCreatesCorrectFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер не создал фильм");
        Assertions.assertEquals("Тестовый фильм", films.iterator().next().getName(), "Контроллер создал некорректный фильм");
    }

    @Test
    void filmControllerRejectsDuplicateFilms() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        Film film2 = new Film();
        film2.setName("Тестовый фильм");
        film2.setDescription("Тестовое описание еще одного фильма");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(90);

        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер не создал фильм");

        DuplicatedDataException thrown = assertThrows(
                DuplicatedDataException.class,
                () -> filmController.create(film2),
                "Контроллер не выкинул исключение о дубликате фильма"
        );

        assertTrue(thrown.getMessage().contains("Фильм с таким названием и датой релиза уже существует"));
    }

    @Test
    void filmControllerRejectsTooOldFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(1850, 1, 1));
        film.setDuration(90);

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> filmController.create(film),
                "Контроллер не выкинул исключение о слишком старом фильме"
        );

        assertTrue(thrown.getMessage().contains("Дата релиза не может быть раньше " +
                Film.CINEMA_BIRTH_DAY.format(DataUtils.DATE_FORMATTER)));
    }

    @Test
    void filmControllerUpdatesFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер не создал фильм");

        Film film2 = new Film();
        film2.setId(film.getId());
        film2.setName("Тестовый фильм");
        film2.setDescription("Тестовое описание фильма");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(120);

        filmController.update(film2);
        films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер создал лишний фильм");

        Assertions.assertEquals(120, films.iterator().next().getDuration(), "Контроллер не обновил фильм");
    }

    @Test
    void filmControllerRejectsUpdateWithoutId() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        ConditionsNotMetException thrown = assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(film),
                "Контроллер не выкинул исключение об отсутствии Id фильма"
        );

        assertTrue(thrown.getMessage().contains("Id должен быть указан"));
    }

    @Test
    void filmControllerRejectsUpdateOfAbsentId() {
        Film film = new Film();
        film.setId(2L);
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.update(film),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Фильм с id = " + film.getId() + " не найден"));
    }
}
