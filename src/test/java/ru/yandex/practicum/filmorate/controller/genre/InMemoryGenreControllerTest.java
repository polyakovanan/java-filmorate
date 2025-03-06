package ru.yandex.practicum.filmorate.controller.genre;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;

@SpringBootTest(classes =
        {GenreController.class,
        GenreService.class,
        InMemoryGenreStorage.class})
class InMemoryGenreControllerTest extends GenreControllerTest {
}
