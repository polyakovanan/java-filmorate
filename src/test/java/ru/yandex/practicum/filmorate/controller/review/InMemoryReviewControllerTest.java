package ru.yandex.practicum.filmorate.controller.review;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.InMemoryMPARatingStorage;
import ru.yandex.practicum.filmorate.storage.review.InMemoryReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@SpringBootTest(classes = {
        ReviewController.class,
        FilmController.class,
        UserController.class,
        ReviewService.class,
        FilmService.class,
        UserService.class,
        InMemoryReviewStorage.class,
        InMemoryFilmStorage.class,
        InMemoryUserStorage.class,
        InMemoryFriendshipStorage.class,
        InMemoryGenreStorage.class,
        InMemoryMPARatingStorage.class,
        InMemoryLikeStorage.class,
        ApplicationContext.class
})
class InMemoryReviewControllerTest extends ReviewControllerTest {
}