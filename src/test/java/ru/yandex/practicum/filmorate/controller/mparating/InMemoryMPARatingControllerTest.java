package ru.yandex.practicum.filmorate.controller.mparating;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.MPARatingController;
import ru.yandex.practicum.filmorate.service.MPARatingService;
import ru.yandex.practicum.filmorate.storage.mparating.InMemoryMPARatingStorage;

@SpringBootTest(classes = {MPARatingController.class, MPARatingService.class, InMemoryMPARatingStorage.class})

class InMemoryMPARatingControllerTest extends MPARatingControllerTest {
}
