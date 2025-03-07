package ru.yandex.practicum.filmorate.controller.director;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.director.InMemoryDirectorStorage;

@SpringBootTest(classes = {
        DirectorController.class,
        DirectorService.class,
        InMemoryDirectorStorage.class
})
public class InMemoryDirectorControllerTest extends DirectorControllerTest {
}
