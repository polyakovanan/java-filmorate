package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {FilmController.class, UserController.class, FilmService.class, UserService.class, InMemoryFilmStorage.class, InMemoryUserStorage.class, ApplicationContext.class})
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        filmStorage.clear();
        userStorage.clear();
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

        assertTrue(thrown.getReason().contains("Дата релиза не может быть раньше " +
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

    @Test
    void filmControllerFindsFilmById() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        Film foundFilm = filmController.findById(1L);
        Assertions.assertEquals(film, foundFilm, "Контроллер нашел не тот фильм по Id");
    }

    @Test
    void filmControllerDoNotFindFilmById() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.findById(1L),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Фильм с id = " + 1L + " не найден"));
    }

    @Test
    void filmControllerAddsLike() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        filmController.addLike(1L, 1L);
        Film foundFilm = filmController.findById(1L);
        Assertions.assertTrue(foundFilm.getLikes().contains(1L), "Контроллер не поставил лайк пользователя");
    }

    @Test
    void filmControllerRefusesAddLikeOfUnknownUser() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.addLike(1L, 1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 1L + " не найден"));
    }

    @Test
    void filmControllerRefusesAddLikeOfUnknownFilm() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.addLike(1L, 1L),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Фильм с id = " + 1L + " не найден"));
    }

    @Test
    void filmControllerRemovesLike() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        filmController.addLike(1L, 1L);
        Film foundFilm = filmController.findById(1L);
        Assertions.assertTrue(foundFilm.getLikes().contains(1L), "Контроллер не поставил лайк пользователя");

        filmController.removeLike(1L, 1L);
        foundFilm = filmController.findById(1L);
        Assertions.assertTrue(foundFilm.getLikes().isEmpty(), "Контроллер не удалил лайк пользователя");

    }

    @Test
    void filmControllerRefusesRemoveLikeOfUnknownUser() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.removeLike(1L, 1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 1L + " не найден"));
    }

    @Test
    void filmControllerRefusesRemoveLikeOfUnknownFilm() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.removeLike(1L, 1L),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Фильм с id = " + 1L + " не найден"));
    }

    @Test
    void filmControllerFindsPopularFilms() {
        Film film = new Film();
        film.setName("Тестовый фильм 1");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        film = new Film();
        film.setName("Тестовый фильм 2");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        film = new Film();
        film.setName("Тестовый фильм 3");
        film.setDescription("Тестовое описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmController.create(film);

        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user = new User();
        user.setLogin("test2");
        user.setName("Тестовый пользователь 2");
        user.setEmail("test2@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        filmController.addLike(2L, 1L);
        filmController.addLike(2L, 2L);
        filmController.addLike(1L, 1L);

        List<Film> films = filmController.findPopular(10);
        Assertions.assertEquals(3, films.size(), "Контроллер неправильно определил количество популярных фильмов");
        Assertions.assertEquals("Тестовый фильм 2", films.get(0).getName(), "Контроллер неправильно определил порядок популярных фильмов");
        Assertions.assertEquals("Тестовый фильм 1", films.get(1).getName(), "Контроллер неправильно определил порядок популярных фильмов");
        Assertions.assertEquals("Тестовый фильм 3", films.get(2).getName(), "Контроллер неправильно определил порядок популярных фильмов");
    }
}
