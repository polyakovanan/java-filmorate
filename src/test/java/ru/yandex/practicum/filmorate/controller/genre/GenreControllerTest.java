package ru.yandex.practicum.filmorate.controller.genre;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class GenreControllerTest {
    @Autowired
    GenreController genreController;

    @Test
    void genreControllerFindsGenreById() {
        Genre genre = genreController.findById(1L);
        Assertions.assertEquals("Комедия", genre.getName(), "Контроллер нашел не тот жанр по Id");
    }

    @Test
    void genreControllerDoNotFindGenreById() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> genreController.findById(100L),
                "Контроллер не выкинул исключение об отсутствии жанра по Id"
        );

        assertTrue(thrown.getMessage().contains("Жанр с id = " + 100L + " не найден"));
    }

    @Test
    void genreControllerFindAll() {
        List<Genre> genres = genreController.findAll();
        assertEquals(6, genres.size(), "Контроллер не нашел все жанры");
    }
}
