package ru.yandex.practicum.filmorate.controller.film;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.DataUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class FilmControllerTest {

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

    @Autowired
    private FriendshipStorage friendshipStorage;

    @Autowired
    private MPARatingStorage mpaRatingStorage;

    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private LikeStorage likeStorage;

    @BeforeEach
    void init() {
        filmStorage.clear();
        userStorage.clear();
        friendshipStorage.clear();
        likeStorage.clear();
    }

    @Test
    void filmControllerCreatesCorrectFilm() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер не создал фильм");
        Assertions.assertEquals("Тестовый фильм", films.iterator().next().getName(), "Контроллер создал некорректный фильм");
    }

    @Test
    void filmControllerRejectsDuplicateFilms() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

        Film film2 = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание еще одного фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

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
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(1850, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

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
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер не создал фильм");

        Film film2 = Film.builder()
                .id(film.getId())
                .name("Тестовый фильм 2")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

        filmController.update(film2);
        films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Контроллер создал лишний фильм");

        Assertions.assertEquals(120, films.iterator().next().getDuration(), "Контроллер не обновил фильм");
    }

    @Test
    void filmControllerRejectsUpdateWithoutId() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

        ConditionsNotMetException thrown = assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(film),
                "Контроллер не выкинул исключение об отсутствии Id фильма"
        );

        assertTrue(thrown.getMessage().contains("Id должен быть указан"));
    }

    @Test
    void filmControllerRejectsUpdateOfAbsentId() {
        Film film = Film.builder()
                .id(2L)
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> filmController.update(film),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Фильм с id = " + film.getId() + " не найден"));
    }

    @Test
    void filmControllerFindsFilmById() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
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
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);

        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        filmController.addLike(1L, 1L);
        Assertions.assertTrue(likeStorage.findAll().stream().anyMatch(l -> l.getFilmId() == 1L && l.getUserId() == 1L), "Контроллер не поставил лайк пользователя");
    }

    @Test
    void filmControllerRefusesAddLikeOfUnknownUser() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);

        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        filmController.addLike(1L, 1L);
        Assertions.assertTrue(likeStorage.findAll().stream().anyMatch(l -> l.getFilmId() == 1L && l.getUserId() == 1L), "Контроллер не поставил лайк пользователя");

        filmController.removeLike(1L, 1L);
        Assertions.assertTrue(likeStorage.findAll().isEmpty(), "Контроллер не удалил лайк пользователя");

    }

    @Test
    void filmControllerRefusesRemoveLikeOfUnknownUser() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        Film film = Film.builder()
                .name("Тестовый фильм 1")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);

        film = Film.builder()
                .name("Тестовый фильм 2")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);

        film = Film.builder()
                .name("Тестовый фильм 3")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();
        filmController.create(film);

        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь 1")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
