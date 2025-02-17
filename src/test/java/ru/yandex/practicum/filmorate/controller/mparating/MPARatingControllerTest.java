package ru.yandex.practicum.filmorate.controller.mparating;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.MPARatingController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class MPARatingControllerTest {
    @Autowired
    MPARatingController mpaRatingController;

    @Test
    void genreControllerFindsGenreById() {
        MPARating mpaRating = mpaRatingController.findById(1L);
        Assertions.assertEquals("G", mpaRating.getName(), "Контроллер нашел не тот рейтинг по Id");
    }

    @Test
    void genreControllerDoNotFindGenreById() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> mpaRatingController.findById(100L),
                "Контроллер не выкинул исключение об отсутствии рейтинга по Id"
        );

        assertTrue(thrown.getMessage().contains("Рейтинг с id = " + 100L + " не найден"));
    }

    @Test
    void genreControllerFindAll() {
        List<MPARating> mpaRatings = mpaRatingController.findAll();
        assertEquals(5, mpaRatings.size(), "Контроллер не нашел все жанры");
    }
}
