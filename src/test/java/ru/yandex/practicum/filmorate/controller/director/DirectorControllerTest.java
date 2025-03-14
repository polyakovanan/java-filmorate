package ru.yandex.practicum.filmorate.controller.director;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class DirectorControllerTest {
    @Autowired
    private DirectorController directorController;

    @Autowired
    private DirectorStorage directorStorage;

    @BeforeEach
    void init() {
        directorStorage.clear();
    }

    @Test
    void directorControllerCreatesDirector() {
        Director director = Director.builder().name("director").build();
        directorController.create(director);
        Collection<Director> directors = directorController.findAll();
        Assertions.assertEquals(1L, directors.size(), "Контроллер не создал режиссера");
    }

    @Test
    void directorControllerGetsDirectorById() {
        Director director = Director.builder().name("director").build();
        directorController.create(director);
        Director director1 = directorController.findById(1L);
        Assertions.assertEquals("director", director1.getName(), "Контроллер не возвращает режиссера по id");
    }

    @Test
    void directorControllerUpdatesDirector() {
        Director director = Director.builder().name("director").build();
        directorController.create(director);
        director.setName("director2");
        directorController.update(director);
        Director director1 = directorController.findById(1L);
        Assertions.assertEquals("director2", director1.getName(), "Контроллер не обновляет режиссера");
    }

    @Test
    void directorControllerDeletesDirector() {
        Director director = Director.builder().name("director").build();
        directorController.create(director);
        directorController.delete(1L);
        Collection<Director> directors = directorController.findAll();
        Assertions.assertEquals(0L, directors.size(), "Контроллер не удаляет режиссера");
    }

    @Test
    void directorControllerUpdateRejectsAbsentId() {
        Director director = Director.builder().name("director").build();
        ConditionsNotMetException thrown = assertThrows(
                ConditionsNotMetException.class,
                () -> directorController.update(director),
                "Контроллер не выкинул исключение об отсутствии id"
        );

        assertTrue(thrown.getMessage().contains("Id должен быть указан"));
    }

    @Test
    void directorControllerUpdateRejectsNotFoundId() {
        Director director = Director.builder().id(1L).name("director").build();
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> directorController.update(director),
                "Контроллер не выкинул исключение об отсутствии режиссера по id"
        );

        assertTrue(thrown.getMessage().contains("Режиссер с id = 1 не найден"));
    }

    @Test
    void directorControllerDeleteRejectsNotFoundId() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> directorController.delete(1L),
                "Контроллер не выкинул исключение об отсутствии режиссера по id"
        );

        assertTrue(thrown.getMessage().contains("Режиссер с id = 1 не найден"));
    }

    @Test
    void directorControllerThrowsNotFoundExceptionOnGetById() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> directorController.findById(1L),
                "Контроллер не выкинул исключение об отсутствии режиссера по id"
        );

        assertTrue(thrown.getMessage().contains("Режиссер с id = 1 не найден"));
    }
}
